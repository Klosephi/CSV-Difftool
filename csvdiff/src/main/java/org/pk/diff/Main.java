package org.pk.diff;

import org.apache.commons.cli.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class Main {

    private static final Logger log = Logger.getLogger(Main.class.getName());


    public static void main (String... args) {
        CommandLineParser parser = new DefaultParser();

        Options options = new Options();
        options.addOption(Option.builder().argName("b").longOpt("base").desc("base csv").hasArg(true).desc("Base file for csv comparison").required().build());
        options.addOption(Option.builder().argName("a").longOpt("actual").desc("actual csv").hasArg(true).desc("Actual file for csv comparison").required().build());
        options.addOption(Option.builder().argName("d").longOpt("destination").desc("base csv").hasArg(true).desc("Destination file for csv comparison").required().build());
        options.addOption(Option.builder().argName("k").longOpt("keys").desc("base csv").hasArgs().desc("Keys").valueSeparator(',').required().build());



        try {
            CommandLine line = parser.parse(options, args);
            String baseFile = line.getOptionValue("base");
            String actualFile = line.getOptionValue("actual");
            String destinationFile = line.getOptionValue("destination");
            String[] keys = line.getOptionValues("keys");

            log.info(baseFile);

            CSVDiff csvDiff = new CSVDiff(keys);
            csvDiff.load(baseFile, actualFile);

            ResultPrinter resultPrinter = new ResultPrinter(new FileWriter(destinationFile));
            resultPrinter.printResult(csvDiff.compare(), csvDiff.getHeaderList());

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
