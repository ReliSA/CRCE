package cz.zcu.kiv.crce.rest.client.indexer.cli;

import java.io.File;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

public class CommandLineInterface {
        private final static String helpShort = "-h";
        private final static String helpLong = "-help";
        private final static String fileShort = "-f";
        private final static String fileLong = "-file";
        private final static String debugShort = "-d";
        private final static String debugLong = "-debug";
        private final static String fileLongFull = fileLong + "=";
        private final static String fileShortFull = fileShort + "=";

        private static String filename;

        /**
         * Creates File object from given filepath
         * 
         * @param args App arguments
         * @return File object
         */
        public static File getFile(String[] args) {
                processArgs(args);
                if (filename == null) {
                        showError();
                        System.exit(-1);
                }

                return new File(filename);
        }

        private static void showTitle() {
                System.out.println(
                                " _______ ______  _                                                                    _               ");
                System.out.println(
                                "(_______|_____ \\| |                                       _                       _  (_)              ");
                System.out.println(
                                " _______ _____) ) |    ____ _____  ____ ___  ____   ___ _| |_  ____ _   _  ____ _| |_ _  ___  ____    ");
                System.out.println(
                                "|  ___  |  ____/| |   / ___) ___ |/ ___) _ \\|  _ \\ /___|_   _)/ ___) | | |/ ___|_   _) |/ _ \\|  _ \\   ");
                System.out.println(
                                "| |   | | |     | |  | |   | ____( (__| |_| | | | |___ | | |_| |   | |_| ( (___  | |_| | |_| | | | |  ");
                System.out.println(
                                "|_|   |_|_|     |_|  |_|   |_____)\\____)___/|_| |_(___/   \\__)_|   |____/ \\____)  \\__)_|\\___/|_| |_|  ");
                System.out.println(
                                "====================================================================================================");

        }

        private static void showError() {
                showHelp();
                System.err.println("Missing the " + fileShort + " or " + fileLong + " param");
        }

        private static void showHelp() {
                showTitle();
                System.out.println("Relative or absolute path to JAR which will be processed: "
                                + fileShort + " | " + fileLong
                                + " = path to example.jar or example.war");
                System.out.println("Debug ingo: " + debugShort + " | " + debugLong + "");
                System.out.println("Manual: " + helpShort + " | " + helpLong + "");

        }

        private static void processArgs(String[] args) {
                boolean debugArg = false;
                boolean helpArg = false;
                for (String arg : args) {
                        if (arg.startsWith(fileShort) || arg.startsWith(fileLong)) {
                                filename = arg.replace(fileShortFull, "").replace(fileLongFull, "");
                        } else if (arg.startsWith(debugShort) || arg.startsWith(debugLong)) {
                                debugArg = true;
                        } else if (arg.startsWith(helpLong) || arg.startsWith(helpShort)) {
                                helpArg = true;
                        }
                }
                if (helpArg) {
                        showHelp();
                        System.exit(0);
                }
                if (!debugArg) {
                        Configurator.setLevel("extractor", Level.OFF);
                }
        }
}
