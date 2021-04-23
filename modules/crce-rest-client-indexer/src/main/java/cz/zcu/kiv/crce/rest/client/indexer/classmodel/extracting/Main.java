package cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting;

import java.io.File;
import java.util.Collection;
import cz.zcu.kiv.crce.rest.client.indexer.cli.CommandLineInterface;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.processor.Processor;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ToStringTools;

public class Main {

    public static void main(String[] args) throws Exception {
        File jarFile = CommandLineInterface.getFile(args);

        if (jarFile == null) {
            return;
        }

        Collection<Endpoint> endpoints = Processor.process(jarFile).values();
        System.out.println(ToStringTools.endpointsToJSON(endpoints));
    }
}
