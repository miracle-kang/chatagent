package com.miraclekang.chatgpt.identity.application.customer;

import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.common.repo.SearchCriteria;
import com.miraclekang.chatgpt.common.repo.SearchSpecification;
import com.miraclekang.chatgpt.identity.application.customer.command.NewCustomerCommand;
import com.miraclekang.chatgpt.identity.application.customer.command.UpdateCustomerCommand;
import com.miraclekang.chatgpt.identity.application.customer.querystack.CustomerDTO;
import com.miraclekang.chatgpt.identity.application.user.querystack.UserEquityInfoDTO;
import com.miraclekang.chatgpt.identity.application.user.querystack.UserProfileDTO;
import com.miraclekang.chatgpt.identity.domain.model.equity.UserEquityInfoService;
import com.miraclekang.chatgpt.identity.domain.model.identity.*;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.PasswordService;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserIdentityCheckService;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.Username;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.miraclekang.chatgpt.common.reactive.ReactiveUtils.blockingOperation;

@Service
public class CustomerService {

    private final PasswordService passwordService;
    private final RegisterService userRegisterService;
    private final UserIdentityCheckService identityCheckService;

    private final CustomerRepository customerRepository;
    private final UserEquityInfoService userEquityInfoService;

    public CustomerService(PasswordService passwordService,
                           RegisterService registerService,
                           UserIdentityCheckService identityCheckService,
                           CustomerRepository customerRepository,
                           UserEquityInfoService userEquityInfoService) {
        this.passwordService = passwordService;
        this.userRegisterService = registerService;
        this.identityCheckService = identityCheckService;
        this.customerRepository = customerRepository;
        this.userEquityInfoService = userEquityInfoService;
    }

    @Transactional
    public Mono<CustomerDTO> addCustomer(NewCustomerCommand command) {
        return Requester.currentRequester()
                .publishOn(Schedulers.boundedElastic())
                .map(requester -> {
                    var customer = userRegisterService.adminRegisterCustomer(
                            requester,
                            Username.of(command.getUsername()),
                            Email.of(command.getEmailAddress()),
                            Phone.of(command.getPhoneNumber()),
                            command.getPassword(),
                            command.getDisabled(),
                            command.getProfile() == null ? null : command.getProfile().toProfile()
                    );
                    return customerRepository.save(customer);
                })
                .flatMap(this::toDTO);
    }

    @Transactional
    public Mono<CustomerDTO> updateCustomer(String aCustomerId, UpdateCustomerCommand command) {
        CustomerId customerId = new CustomerId(aCustomerId);
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(customerId)
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(customerRepository::findByCustomerId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found")))
                        .map(customer -> {
                            customer.update(
                                    Username.of(command.getUsername()),
                                    Email.of(command.getEmailAddress()),
                                    Phone.of(command.getPhoneNumber()),
                                    command.getProfile() == null ? null : command.getProfile().toProfile(),
                                    requester, identityCheckService
                            );
                            return customerRepository.save(customer);
                        }))
                .flatMap(this::toDTO);
    }

    @Transactional
    public Mono<Void> deleteCustomer(String aCustomerId) {
        CustomerId customerId = new CustomerId(aCustomerId);
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(customerId)
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(customerRepository::findByCustomerId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found")))
                        .flatMap(customer -> blockingOperation(() -> customerRepository.delete(customer))))
                .then();
    }

    public Mono<CustomerDTO> getCustomer(String aCustomerId) {
        CustomerId customerId = new CustomerId(aCustomerId);
        return Mono.just(customerId)
                .publishOn(Schedulers.boundedElastic())
                .mapNotNull(customerRepository::findByCustomerId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found")))
                .flatMap(this::toDTO);
    }

    public Mono<Page<CustomerDTO>> queryCustomers(List<SearchCriteria> searchCriteria, Pageable pageable) {
        return Requester.currentRequester()
                .publishOn(Schedulers.boundedElastic())
                .flatMap(requester -> {
                    PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                            Sort.by(Administrator_.ID).descending());

                    Page<Customer> page = customerRepository.findAll(SearchSpecification.allOf(searchCriteria), pageRequest);
                    return Flux.fromIterable(page.getContent())
                            .flatMap(this::toDTO)
                            .collectList()
                            .map(customers -> new PageImpl<>(customers, page.getPageable(), page.getTotalElements()));
                });
    }

    public Mono<Void> disableCustomer(String aCustomerId) {
        CustomerId customerId = new CustomerId(aCustomerId);
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(customerId)
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(customerRepository::findByCustomerId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found")))
                        .map(customer -> {
                            customer.disable(requester);
                            return customerRepository.save(customer);
                        }).then());
    }

    public Mono<Void> enableCustomer(String aCustomerId) {
        CustomerId customerId = new CustomerId(aCustomerId);
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(customerId)
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(customerRepository::findByCustomerId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found")))
                        .map(customer -> {
                            customer.enable(requester);
                            return customerRepository.save(customer);
                        }).then());
    }

    public Mono<Void> resetCustomerPassword(String aCustomerId) {
        CustomerId customerId = new CustomerId(aCustomerId);
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(customerId)
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(customerRepository::findByCustomerId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer not found")))
                        .map(customer -> {
                            customer.resetPassword(requester, passwordService);
                            return customerRepository.save(customer);
                        }).then());
    }

    private Mono<CustomerDTO> toDTO(Customer customer) {
        if (customer == null) {
            return Mono.just(new CustomerDTO());
        }
        return userEquityInfoService.userEquities(customer.getUser().getUserId()).collectList()
                .switchIfEmpty(Mono.just(List.of()))
                .map(userEquities -> new CustomerDTO(
                        customer.getCustomerId().getId(),
                        customer.getUser().getUserId().getId(),
                        customer.getUser().getUsername().getUsername(),
                        customer.getUser().getEmail() == null ? null : customer.getUser().getEmail().getAddress(),
                        customer.getUser().getPhone() == null ? null : customer.getUser().getPhone().getNumber(),
                        customer.getUser().getDisabled(),
                        UserProfileDTO.from(customer.getUser().getProfile()),
                        userEquities.stream().map(UserEquityInfoDTO::from).toList(),
                        customer.getOperator() == null ? null : customer.getOperator().getUserName()
                ));
    }
}
