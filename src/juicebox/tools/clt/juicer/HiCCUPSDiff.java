/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2016 Broad Institute, Aiden Lab
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package juicebox.tools.clt.juicer;

import juicebox.tools.clt.CommandLineParserForJuicer;
import juicebox.tools.clt.JuicerCLT;
import juicebox.track.feature.Feature2D;

/**
 * Developed by Suhas Rao, ported by Suhas Rao + Neva Durand
 */
public class HiCCUPSDiff extends JuicerCLT {

    public HiCCUPSDiff() {
        super("hiccupsdiff [-m matrixSize] [-k normalization (NONE/VC/VC_SQRT/KR)] [-c chromosome(s)] [-r resolution(s)] " +
                "[-f fdr] [-p peak width] [-i window] [-t thresholds] [-d centroid distances] " +
                "<firstHicFile(s)> <secondHicFile(s)> <firstLoopList> <secondLoopList> <outputDirectory>");

    }

    @Override
    protected void readJuicerArguments(String[] args, CommandLineParserForJuicer juicerParser) {
        if (args.length != 5) {
            printUsage();
        }

        HiCCUPS hiccups1 = new HiCCUPS();
        //hiccups1.readJuicerArguments();

    }

    @Override
    public void run() {
                       /*
                       #!/bin/bash

# looplist1 is the loop list generated by running HiCCUPS on hic_file1, looplist2 is the loop list generated by running HiCCUPS on hic_file2
hic_file1=$1
hic_file2=$2
looplist1=$3
looplist2=$4

# the output directory
output_dir=$5

# make output directory
mkdir $output_dir

# make output directories for HiCCUPS runs
mkdir $ouptut_dir"/file1"
mkdir $output_dir"/file2"

# run HiCCUPS on hic_file1 with the list from hic_file2 and vice versa
# I forget what the default parameters are but only resolutions that are present in the provided looplists need to be called
/aidenlab/work/suhas/analysis/12.18.15_test_juicebox/juicebox.sh hiccups $hic_file1 $output_dir"/file1" $looplist2
/aidenlab/work/suhas/analysis/12.18.15_test_juicebox/juicebox.sh hiccups $hic_file2 $output_dir"/file2" $looplist1

# identify conserved loops, parameters from RH2014
/aidenlab/work/suhas/scripts/loop_analysis/centroid_reproducibility_matrix2.py $looplist1 $looplist2 50000 0.2 > $output_dir"/conserved_loops_list2.txt"
/aidenlab/work/suhas/scripts/loop_analysis/centroid_reproducibility_matrix2.py $looplist2 $looplist1 50000 0.2 > $output_dir"/conserved_loops_list1.txt"

# identify differential loops

maxenrich=1.3 #this can be made an input parameter

# print candidate diff loops
awk -v cllist=$output_dir"/conserved_loops_list1.txt" 'BEGIN{while(getline<cllist>0){peak[$1 " " $2 " " $3 " " $4 " " $5 " " $6]++}}{if (NR==1) {print $0} else if (peak[$1 " " $2 " " $3 " "$4 " " $5 " " $6]=="") {print $0}}' $looplist1 > $output_dir"/tmp1"
awk -v cllist=$output_dir"/conserved_loops_list2.txt" 'BEGIN{while(getline<cllist>0){peak[$1 " " $2 " " $3 " " $4 " " $5 " " $6]++}}{if (NR==1) {print $0} else if (peak[$1 " " $2 " " $3 " "$4 " " $5 " " $6]=="") {print $0}}' $looplist2 > $output_dir"/tmp2"

# print diff loops under max enrich parameter for all filters
awk -v list5kb=$output_dir"/file1/requested_list_5000" -v list10kb=$output_dir"/file1/requested_list_10000" -v list25kb=$output_dir"/file1/requested_list_25000" -v me=$maxenrich 'BEGIN{while(getline<list5kb>0) {if ($8<me*$9&&$8<me*$10&&$8<me*$11&&$8<me*$12) {peak[$1 " " $2 " " $3 " " $4 " " $5 " " $6]++}} while(getline<list10kb>0){if ($8<me*$9&&$8<me*$10&&$8<me*$11&&$8<me*$12) {peak[$1 " " $2 " " $3 " " $4 " " $5 " " $6]++}} while(getline<list25kb>0){if ($8<me*$9&&$8<me*$10&&$8<me*$11&&$8<me*$12) {peak[$1 " " $2 " " $3 " " $4 " " $5 " " $6]++}}}{if (NR==1) {print $0} else if {peak[$1 " " $2 " " $3 " " $4 " " $5 " " $6]>0) {print $0}}' $output_dir"/tmp2" > $output_dir"/differential_loops_list2.txt"
awk -v list5kb=$output_dir"/file2/requested_list_5000" -v list10kb=$output_dir"/file2/requested_list_10000" -v list25kb=$output_dir"/file2/requested_list_25000" -v me=$maxenrich 'BEGIN{while(getline<list5kb>0) {if ($8<me*$9&&$8<me*$10&&$8<me*$11&&$8<me*$12) {peak[$1 " " $2 " " $3 " " $4 " " $5 " " $6]++}} while(getline<list10kb>0){if ($8<me*$9&&$8<me*$10&&$8<me*$11&&$8<me*$12) {peak[$1 " " $2 " " $3 " " $4 " " $5 " " $6]++}} while(getline<list25kb>0){if ($8<me*$9&&$8<me*$10&&$8<me*$11&&$8<me*$12) {peak[$1 " " $2 " " $3 " " $4 " " $5 " " $6]++}}}{if (NR==1) {print $0} else if {peak[$1 " " $2 " " $3 " " $4 " " $5 " " $6]>0) {print $0}}' $output_dir"/tmp1" > $output_dir"/differential_loops_list1.txt"
rm $output_dir"/tmp1"
rm $output_dir"/tmp2"

                        */
    }
}