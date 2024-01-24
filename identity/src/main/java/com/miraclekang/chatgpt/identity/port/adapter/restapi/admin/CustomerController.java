package com.miraclekang.chatgpt.identity.port.adapter.restapi.admin;

import com.miraclekang.chatgpt.common.restapi.SearchCriteriaParam;
import com.miraclekang.chatgpt.identity.application.customer.CustomerService;
import com.miraclekang.chatgpt.identity.application.customer.command.NewCustomerCommand;
import com.miraclekang.chatgpt.identity.application.customer.command.UpdateCustomerCommand;
import com.miraclekang.chatgpt.identity.application.customer.querystack.CustomerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/admin/customers")
@Tag(name = "Customer APIs", description = "Customer APIs")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @Operation(summary = "Add customer", description = "Add a new customer")
    public Mono<CustomerDTO> addCustomer(@Valid @RequestBody NewCustomerCommand command) {
        return customerService.addCustomer(command);
    }

    @PutMapping("/{customerId}")
    @Operation(summary = "Update customer", description = "Update a customer")
    public Mono<CustomerDTO> updateCustomer(@PathVariable String customerId,
                                            @Valid @RequestBody UpdateCustomerCommand command) {
        return customerService.updateCustomer(customerId, command);
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer", description = "Get a customer")
    public Mono<CustomerDTO> getCustomer(@PathVariable String customerId) {
        return customerService.getCustomer(customerId);
    }

    @DeleteMapping("/{customerId}")
    @Operation(summary = "Delete customer", description = "Delete a customer")
    public Mono<Void> deleteCustomer(@PathVariable String customerId) {
        return customerService.deleteCustomer(customerId);
    }

    @GetMapping
    @PageableAsQueryParam
    @Operation(summary = "Query customers", description = "Query customers")
    public Mono<Page<CustomerDTO>> queryCustomers(@ParameterObject SearchCriteriaParam criteriaParam,
                                                  @ParameterObject Pageable pageable) {
        return customerService.queryCustomers(criteriaParam.toCriteria(CustomerDTO.searchKeyMapping), pageable);
    }

    @PutMapping("/{customerId}/enable")
    @Operation(summary = "Enable customer", description = "Enable a customer")
    public Mono<Void> enableCustomer(@PathVariable String customerId) {
        return customerService.enableCustomer(customerId);
    }

    @PutMapping("/{customerId}/disable")
    @Operation(summary = "Disable customer", description = "Disable a customer")
    public Mono<Void> disableCustomer(@PathVariable String customerId) {
        return customerService.disableCustomer(customerId);
    }

    @PutMapping("/{customerId}/reset-password")
    @Operation(summary = "Reset password", description = "Reset password")
    public Mono<Void> resetPassword(@PathVariable String customerId) {
        return customerService.resetCustomerPassword(customerId);
    }
}
