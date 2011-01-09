package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.internal.wrapper.ConvertedResource;
import cz.zcu.kiv.crce.metadata.Resource;
import org.apache.felix.bundlerepository.impl.CapabilityImpl;
import org.apache.felix.bundlerepository.impl.RequirementImpl;
import cz.zcu.kiv.crce.metadata.DataModelHelperExt;
import cz.zcu.kiv.crce.metadata.internal.wrapper.Wrapper;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import org.apache.felix.bundlerepository.impl.DataModelHelperImpl;
import org.apache.felix.bundlerepository.impl.PullParser;
import org.apache.felix.bundlerepository.impl.RepositoryParser;
import org.apache.felix.bundlerepository.impl.ResourceImpl;
import org.apache.felix.bundlerepository.impl.XmlWriter;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import static org.apache.felix.bundlerepository.impl.RepositoryParser.*;
/**
 *
 * @author kalwi
 */
public class DataModelHelperExtImpl extends DataModelHelperImpl implements DataModelHelperExt {

    @Override
    public String writeMetadata(Resource resource) {
        try {
            StringWriter sw = new StringWriter();
            writeMetadata(resource, sw);
            return sw.toString();
        } catch (IOException e) {
            IllegalStateException ex = new IllegalStateException(e);
            ex.initCause(e);
            throw ex;
        }
    }

    @Override
    public void writeMetadata(Resource resource, Writer writer) throws IOException {
        XmlWriter w = new XmlWriter(writer);

        org.apache.felix.bundlerepository.Resource felixResource = Wrapper.wrap(resource);

        w.element(OBR);

//        if (resource.getSymbolicName() == null) {
//
//            w.element(RepositoryParser.RESOURCE)
//                .attribute(Resource.ID, resource.getId())
//                .attribute(Resource.SYMBOLIC_NAME, resource.getSymbolicName())
//                .attribute(Resource.PRESENTATION_NAME, resource.getPresentationName())
//                .attribute(Resource.URI, getRelativeUri(resource, Resource.URI))
//                .attribute(Resource.VERSION, resource.getVersion().toString());
//
//            w.textElement(Resource.DESCRIPTION, resource.getProperties().get(Resource.DESCRIPTION))
//                .textElement(Resource.SIZE, resource.getProperties().get(Resource.SIZE))
//                .textElement(Resource.DOCUMENTATION_URI, getRelativeUri(resource, Resource.DOCUMENTATION_URI))
//                .textElement(Resource.SOURCE_URI, getRelativeUri(resource, Resource.SOURCE_URI))
//                .textElement(Resource.JAVADOC_URI, getRelativeUri(resource, Resource.JAVADOC_URI))
//                .textElement(Resource.LICENSE_URI, getRelativeUri(resource, Resource.LICENSE_URI));
//            
//            for (String category : resource.getCategories()) {
//                w.element(RepositoryParser.CATEGORY)
//                    .attribute(RepositoryParser.ID, category)
//                    .end();
//                
//            }
//            
//            for (Capability capability : resource.getCapabilities()) {
//                writeCapability(capability, writer);
//            }
//            
//            for (Requirement requirement : resource.getRequirements()) {
//                writeRequirement(requirement, writer);
//            }
//            
//            w.end();
//        } else {
        writeResource(felixResource, writer);
//        }

        w.end();
    }

    @Override
    public ResourceImpl createResource(URL bundleUrl) throws IOException {
        ResourceImpl resource = null;

        try {
            resource = (ResourceImpl) super.createResource(bundleUrl);
        } catch (IllegalArgumentException e) {
            // not a bundle
            // TODO - atach some other pluginable resource creators, e.g. for CoSi bundles
            return null;
        }

        return resource;
    }

    @Override
    public Resource readMetadata(String xml) throws IOException, Exception {
        try {
            return readMetadata(new StringReader(xml));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Resource readMetadata(Reader reader) throws IOException, Exception {
        XmlPullParser parser = new KXmlParser();
        parser.setInput(reader);

        int event = parser.nextTag();
        if (event != XmlPullParser.START_TAG || !OBR.equals(parser.getName())) {
            throw new Exception("Expected element " + OBR);
        }

        return new ConvertedResource(parseMetadata(parser));
    }

    public ResourceImpl parseMetadata(XmlPullParser reader) throws Exception {
        PullParser parser = new PullParser();
        int event;
        ResourceImpl resource = null;
        
        while ((event = reader.nextTag()) == XmlPullParser.START_TAG) {
            String element = reader.getName();
            
            if (RepositoryParser.RESOURCE.equals(element)) {
                resource = parser.parseResource(reader);
                event = reader.nextTag();
                break;
            } else if (resource == null) {
                resource = new ResourceImpl();
            }
            
            if (CATEGORY.equals(element)) {
                String category = parser.parseCategory(reader);
                resource.addCategory(category);
            } else if (CAPABILITY.equals(element)) {
                CapabilityImpl capability = parser.parseCapability(reader);
                resource.addCapability(capability);
            } else if (REQUIRE.equals(element)) {
                RequirementImpl requirement = parser.parseRequire(reader);
                resource.addRequire(requirement);
            } else {
                StringBuffer sb = null;
                String type = reader.getAttributeValue(null, "type");
                while ((event = reader.next()) != XmlPullParser.END_TAG) {
                    switch (event) {
                        case XmlPullParser.START_TAG:
                            throw new Exception("Unexpected element inside <require/> element");
                        case XmlPullParser.TEXT:
                            if (sb == null) {
                                sb = new StringBuffer();
                            }
                            sb.append(reader.getText());
                            break;
                    }
                }
                if (sb != null) {
                    resource.put(element, sb.toString().trim(), type);
                }
            }

        }

        if (event != XmlPullParser.END_TAG || !OBR.equals(reader.getName())) {
            throw new Exception("Unexpected state");
        }

        return resource;

    }


}
