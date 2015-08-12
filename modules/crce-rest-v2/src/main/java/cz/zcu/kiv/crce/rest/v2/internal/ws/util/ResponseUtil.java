package cz.zcu.kiv.crce.rest.v2.internal.ws.util;

import java.io.File;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.rest.v2.internal.Activator;

/**
 * Date: 2.8.15
 *
 * @author Jakub Danek
 */
public class ResponseUtil {

    public static Response serveResourceAsFile(Resource resource) {
        MetadataService metadataService = Activator.instance().getMetadataService();

        File resourceFile = new File(metadataService.getUri(resource));

        Attribute<String> attribute = metadataService.getIdentity(resource)
                .getAttribute(new SimpleAttributeType<>("mime", String.class));

        String filename = metadataService.getFileName(resource);

        return Response.ok(resourceFile)
                .type(attribute != null ? attribute.getValue() : MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition", "attachment; filename = " + filename)
                .build();
    }
}
