package net.greenmanov.anime.ImageSorter;


import net.greenmanov.anime.ImageSorter.fetching.AFetcher;
import net.greenmanov.anime.ImageSorter.fetching.FilenameFetcher;
import net.greenmanov.anime.ImageSorter.fetching.HumanInfoFetcher;
import net.greenmanov.anime.ImageSorter.fetching.IqdbFetcher;
import net.greenmanov.anime.ImageSorter.sorting.Sorter;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Commandline {
    private static final Logger LOGGER = LogManager.getLogger(Commandline.class.getName());

    private static final String FROM = "f";
    private static final String TO = "t";
    private static final String SUBDIRS = "s";
    private static final String HELP = "H";
    private static final String DELAY = "d";
    private static final String NO_MATCH = "n";
    private static final String MIN_MATCH = "m";

    private Commandline() {
    }

    public static void main(String[] args) {
        Commandline commandline = new Commandline();
        commandline.run(args);
    }

    /**
     * Run commandline interface
     *
     * @param args commandline arguments
     */
    private void run(String[] args) {
        Options options = buildOptions();

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.getArgs().length > 0) {
                String command = commandLine.getArgs()[0];
                if (command.equals("sort")) {
                    sort(commandLine);
                    return;
                } else if (command.contains("fetch:")) {
                    switch (command.replace("fetch:", "")) {
                        case "iqdb":
                            fetch(new IqdbFetcher(), commandLine);
                            break;
                        case "filename":
                            fetch(new FilenameFetcher(), commandLine);
                            break;
                        case "human":
                            fetch(new HumanInfoFetcher(), commandLine);
                            break;
                    }
                    return;
                }
            }
            printHelp(options);
        } catch (ParseException e) {
            System.out.println("Try using -h for help");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Fetch files in directory
     * @param fetcher Fetcher
     * @param commandLine CommandLine object
     */
    private void fetch(AFetcher fetcher, CommandLine commandLine) {
        if (!commandLine.hasOption(FROM)) {
            throw new IllegalArgumentException("Need path to from directory");
        }
        System.out.println(commandLine.getOptionValue(FROM));
        Path from = Paths.get(commandLine.getOptionValue(FROM));
        Path to = commandLine.hasOption(TO) ? Paths.get(commandLine.getOptionValue(TO)) : from;
        boolean hadTo = commandLine.hasOption(TO);
        boolean subdirs = commandLine.hasOption(SUBDIRS);

        try {
            int delay = commandLine.hasOption(DELAY) ? Integer.valueOf(commandLine.getOptionValue(DELAY)) : 750;
            int minSim = commandLine.hasOption(MIN_MATCH) ? Integer.valueOf(commandLine.getOptionValue(MIN_MATCH)) : 80;
            Path noMatch = commandLine.hasOption(NO_MATCH) ? Paths.get(commandLine.getOptionValue(NO_MATCH)) : null;

            try {
                if (!subdirs) {
                    fetcher.fetch(from, to, minSim, delay, noMatch);
                } else {
                    try (Stream<Path> paths = Files.walk(to)) {
                        paths.filter(Files::isDirectory).forEach(p -> {
                            try {
                                Path pt = hadTo ? to : p;
                                fetcher.fetch(p, pt, minSim, delay, noMatch);
                            } catch (InterruptedException e) {
                                LOGGER.error(e);
                            }
                        });
                    } catch (IOException e) {
                        LOGGER.error("Error while walking thought subdirectories", e);
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number", e);
        }
    }

    /**
     * Sort files in directory
     *
     * @param commandLine CommandLine object
     */
    private void sort(CommandLine commandLine) {
        if (!commandLine.hasOption(FROM)) {
            throw new IllegalArgumentException("Need path to from directory");
        }
        Path from = Paths.get(commandLine.getOptionValue(FROM));
        boolean hadTo = commandLine.hasOption(TO);
        Path to = commandLine.hasOption(TO) ? Paths.get(commandLine.getOptionValue(TO)) : from;
        boolean subdirs = commandLine.hasOption(SUBDIRS);

        Sorter sorter = new Sorter();
        if (!subdirs) {
            sorter.sort(from, to);
        } else {
            try (Stream<Path> paths = Files.walk(to)) {
                paths.filter(Files::isDirectory).forEach(p -> {
                    Path pt = hadTo ? to : p;
                    sorter.sort(p, pt);
                });
            } catch (IOException e) {
                LOGGER.error("Error while walking thought subdirectories", e);
            }
        }
    }

    /**
     * Prints help
     *
     * @param options Options for help
     */
    private void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("fetch:{iqdb|filename|human}|sort", options);
    }

    /**
     * Build options for cmd
     *
     * @return Options object
     */
    private Options buildOptions() {
        Options options = new Options();

        // Common
        options.addOption(SUBDIRS, "subdirs", false, "Also parse images from subdirectories");
        options.addOption(FROM, "from", true, "Specify source folder for images");
        options.addOption(TO, "to", true, "Destination folder for images, defaults to from folder");
        options.addOption(HELP, "help", false, "Prints this message");

        // Fetching
        options.addOption(DELAY, "delay", true, "Delay between API calls in ms [750]");
        options.addOption(NO_MATCH, "noMatch", true, "Destination folder for images with no match");
        options.addOption(MIN_MATCH, "minSimilarity", true, "Minimal similarity for image info fetch, from 0 to 100 " +
                "[80]");

        return options;
    }


}
