package org.openpaas.paasta.marketplace.api.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.javaswift.joss.model.StoredObject;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.SoftwareSpecification;
import org.openpaas.paasta.marketplace.api.domain.SoftwareHistory;
import org.openpaas.paasta.marketplace.api.domain.SoftwareHistorySpecification;
import org.openpaas.paasta.marketplace.api.domain.Yn;
import org.openpaas.paasta.marketplace.api.domain.Software.Status;
import org.openpaas.paasta.marketplace.api.service.SoftwareService;
import org.openpaas.paasta.marketplace.api.storageApi.config.SwiftOSConstants;
import org.openpaas.paasta.marketplace.api.storageApi.store.swift.SwiftOSFileInfo;
import org.openpaas.paasta.marketplace.api.storageApi.store.swift.SwiftOSService;
import org.openpaas.paasta.marketplace.api.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/softwares")
@RequiredArgsConstructor
public class SoftwareController {

    private final SoftwareService softwareService;

    @Autowired
    SwiftOSService swiftOSService;

    @GetMapping("/page")
    public Page<Software> getPage(SoftwareSpecification spec, Pageable pageable, HttpServletRequest httpServletRequest) {
        //System.out.println("bearer 토큰 ::: " + httpServletRequest.getHeader("cf-Authorization"));

        spec.setStatus(Status.Approval);
        spec.setInUse(Yn.Y);

        return softwareService.getPage(spec, pageable);
    }

    @GetMapping("/my/page")
    public Page<Software> getMyPage(SoftwareSpecification spec, Pageable pageable) {
        spec.setCreatedBy(SecurityUtils.getUserId());

        return softwareService.getPage(spec, pageable);
    }

    @GetMapping("/{id}")
    public Software get(@NotNull @PathVariable Long id) {
        return softwareService.get(id);
    }

    @PostMapping
    public Software create(@NotNull @Validated(Software.Create.class) @RequestBody Software software,
            BindingResult bindingResult) throws BindException {
        Software sameName = softwareService.getByName(software.getName());
        if (sameName != null) {
            bindingResult.rejectValue("name", "Unique");
        }

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        return softwareService.create(software);
    }

    @PutMapping("/{id}")
    public Software update(@PathVariable @NotNull Long id,
            @NotNull @Validated(Software.Update.class) @RequestBody Software software, BindingResult bindingResult)
            throws BindException {
        Software saved = softwareService.get(id);
        SecurityUtils.assertCreator(saved);

        Software sameName = softwareService.getByName(software.getName());
        if (sameName != null && id != sameName.getId()) {
            bindingResult.rejectValue("name", "Unique");
        }

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        software.setId(id);

        return softwareService.update(software);
    }

    @GetMapping("/{id}/histories")
    public List<SoftwareHistory> getHistoryList(@NotNull @PathVariable Long id, Sort sort) {
        Software software = softwareService.get(id);
        SecurityUtils.assertCreator(software);

        SoftwareHistorySpecification spec = new SoftwareHistorySpecification();
        spec.setSoftwareId(id);

        return softwareService.getHistoryList(spec, sort);
    }


    public Object getObjectDownload(String name) throws IOException {
        final StoredObject object = swiftOSService.getRawObject( name );
        if (null == object) {

            //return createResponseEntity( new byte[0], null, HttpStatus.NOT_FOUND );
        }

        final SwiftOSFileInfo fileInfo = SwiftOSFileInfo.newInstanceFromStoredObject( object );
        if (null == fileInfo) {

            return null;
        }

        final HttpHeaders headers = new HttpHeaders();

        // use SwiftOSFileInfo.getFilename() instead of name(stored filename)
        headers.add( "Content-Disposition", ( "attachment;filename=" + fileInfo.getFilename() ) );
        headers.add( "Content-Transfer-Encoding", "binary" );


        // use SwiftOSFileInfo.getFileType() instead of StoredObject.getContentType()
        headers.add( "Content-Type", fileInfo.getFileType() );


        byte[] rawContents = object.downloadObject();

        return rawContents;
    }

}
