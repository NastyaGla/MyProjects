package com.example.fileparser.util;

import com.beust.jcommander.Parameter;
import lombok.Getter;

@Getter
public class FileParserArgs {
   /* @Parameter(
            names = "--fqc",
            description = "Files queue capacity",
            required = true
    )
    private int fqc;
    @Parameter(
            names = "--jqc",
            description = "Json queue capacity",
            required = true
    )
    private int jqc;
    @Parameter(
            names = "--iqc",
            description = "Images queue capacity",
            required = true
    )
    private int iqc;
    @Parameter(
            names = "--tqc",
            description = "Text queue capacity",
            required = true
    )
    private int tqc;
    @Parameter(
            names = "--tps",
            description = "Thread pool size",
            required = true
    )
    private int tps;*/
    @Parameter(
            names = "--ifp",
            description = "Input folder path",
            required = true)
    private String ifp;
    @Parameter(
            names = "--ofp",
            description = "Output folder path",
            required = true)
    private String ofp;
    @Parameter(names = "--help",  help = true)
    private boolean help;
}

