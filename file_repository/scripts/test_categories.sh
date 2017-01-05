#!/bin/bash

# This script supports generic test case categories. The test case categories
# are specified in files with names matching category_*.cat in grading_tests.
# Here is an example category file:
#
# Sample Category Title
# test_1.txt
# test_5.txt
# test_7.txt
#
# The first line of the file is treated as the human readable title for the
# category which is displayed to the student in the output. The rest of the
# lines list the input file names for test cases in that category.
#
# This script also supports evaluating outputs by using scripts. For a given
# test e.g. test.txt, it searches for test.txt.evaluator first (which must
# be executable), if found, it executes that script passing the generated
# output file to the script. It then checks the exit code of the script to
# determine if the test was passed (0) or failed (1). If it cannot find the
# .txt.evaluator file, it falls back to comparing the results with the
# .txt.expected file using diff -Bw.

grading_tests=$1;
executable=$2;
scratch_path=$3;

sandbox="/usr/local/bin/simple_sandbox";
nobody=65534;       # Usually 99 in RHEL, 65534 in Ubuntu
nogroup=65534;      # Usually 99 in RHEL, 65534 in Ubuntu

array_sum()
{
    # $@: an array of numbers expanded
    # Returns: sum of array elements
    local total=0
    local i=0
    local arr=("$@")
    for i in ${arr[@]}; do
        let total+=$i
    done
    echo $total
}

category_files=$(find $grading_tests -type f -name "category_*.cat" | sort);
declare -a passing
declare -a failing
declare -a total
declare -a titles

let category_count=0
for cf in $category_files; do
    let line_count=0;
    let passing_count=0;
    let failing_count=0;
    let test_count=0;
    while read line; do
        line_count=$((line_count+1));
        if [ "$line_count" -eq "1" ]; then
            # First line: category title
            titles+=("$line")
        elif [[ $line == *.txt ]]; then
            # Other lines: test case file name
            input_file=${grading_tests}/${line}
            expected_file=${grading_tests}/${line}".expected"
            eval_script=${grading_tests}/${line}".evaluator"
            output_file=${scratch_path}/${line}".output";
            diff_file=${scratch_path}/${line}".diff";

            $sandbox -u $nobody -g $nogroup -t 500 $executable <$input_file >$output_file;

            # Evaluating the output
            if [ -x "$eval_script" ]; then
                # evaluator script exists and is executable
                ${eval_script} ${output_file} &> /dev/null
                failed=$?
            else
                diff -Bw $output_file $expected_file > $diff_file;
                if [ -s $diff_file ]; then
                    failed=1
                else
                    failed=0
                fi
            fi

            if [ "$failed" -eq "1" ]; then
                failing_count=$((failing_count+1));
                #echo -e ${line} "0";
            else
                passing_count=$((passing_count+1));
                #echo -e ${line} "1";
            fi
            test_count=$((test_count+1));
            rm -f $output_file;
            rm -f $diff_file;
        fi
    done < "$cf"
    passing+=("$passing_count")
    failing+=("$failing_count")
    total+=("$test_count")
    category_count=$((category_count+1));
done

passing_overall=$(array_sum "${passing[@]}")
failing_overall=$(array_sum "${failing[@]}")
total_overall=$(array_sum "${total[@]}")

for i in $(seq 1 $category_count); do
    j=$((i-1));
    echo ${titles[j]}": ${passing[j]}/${total[j]}"
done
