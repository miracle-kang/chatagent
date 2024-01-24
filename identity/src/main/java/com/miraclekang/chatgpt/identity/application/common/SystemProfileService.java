package com.miraclekang.chatgpt.identity.application.common;

import com.miraclekang.chatgpt.identity.application.common.command.UpdateSystemProfileCommand;
import com.miraclekang.chatgpt.identity.application.common.querystack.SystemProfileDTO;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.identity.domain.model.file.FileReference;
import com.miraclekang.chatgpt.identity.domain.model.file.FileStorageService;
import com.miraclekang.chatgpt.identity.domain.model.system.SystemProfile;
import com.miraclekang.chatgpt.identity.domain.model.system.SystemProfileRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class SystemProfileService {

    private final FileStorageService fileStorageService;
    private final SystemProfileRepository systemProfileRepository;

    public SystemProfileService(FileStorageService fileStorageService,
                                SystemProfileRepository systemProfileRepository) {
        this.fileStorageService = fileStorageService;
        this.systemProfileRepository = systemProfileRepository;
    }


    public Mono<SystemProfileDTO> update(UpdateSystemProfileCommand aCommand) {
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(aCommand)
                        .publishOn(Schedulers.boundedElastic())
                        .map(command -> {
                            SystemProfile profile = systemProfileRepository.systemConfig();
                            profile.update(command.getName(),
                                    command.getAnnouncement(),
                                    command.getWechatGroupQRLightImageFileId() == null ? null
                                            : new FileReference(command.getWechatGroupQRLightImageFileId()),
                                    command.getWechatGroupQRDarkImageFileId() == null ? null
                                            : new FileReference(command.getWechatGroupQRDarkImageFileId()));
                            return systemProfileRepository.save(profile);
                        })
                        .map(systemProfile -> toDTO(systemProfile, requester)));
    }

    public Mono<SystemProfileDTO> get() {
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(systemProfileRepository.systemConfig())
                        .map(systemProfile -> toDTO(systemProfile, requester)));
    }

    private SystemProfileDTO toDTO(SystemProfile aSystemProfile, Requester requester) {
        return new SystemProfileDTO(
                aSystemProfile.getName(),
                aSystemProfile.getAnnouncement(),
                aSystemProfile.getWechatGroupQRLightImage() == null ? null
                        : fileStorageService.previewUrl(aSystemProfile.getWechatGroupQRLightImage().fileId(), requester),
                aSystemProfile.getWechatGroupQRDarkImage() == null ? null
                        : fileStorageService.previewUrl(aSystemProfile.getWechatGroupQRDarkImage().fileId(), requester)
        );
    }
}
