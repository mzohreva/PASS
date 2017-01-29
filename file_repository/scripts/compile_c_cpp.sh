#! /bin/bash

submission_path=$1;
executable=$2;
scratch_path=$3;
auxiliary_files_path=$4;
common_options=$5;
c_options=$6;
cpp_options=$7;

compile_timeout="120s"

find $submission_path -type f -exec cp {} $scratch_path/ \;
find $auxiliary_files_path -type f -exec cp -f {} $scratch_path/ \;
cd $scratch_path

ret=1;
declare -a to_link
linker_to_use="gcc"     # Unless we see a C++ file
files=$(find . -type f                 \
             -name "*.[cC]" -o         \
             -name "*.[cC][pP][pP]" -o \
             -name "*.[cC][cC]");

count=0;
for source_file in $files; do
    count=$((count+1))
    source_file=`basename $source_file`         # To remove the ./ prefix
    object_file=$source_file".o"
    # NOTE: the space between : and -2 is very important.
    if [ ${source_file: -2} == ".c" ] || [ ${source_file: -2} == ".C" ]; then
        opts="$common_options $c_options";
        echo -e "Compiling $source_file with gcc $opts";
        timeout --signal=KILL $compile_timeout gcc $opts -c $source_file -o $object_file;
        ret=$?;
    else
        opts="$common_options $cpp_options";
        echo -e "Compiling $source_file with g++ $opts";
        timeout --signal=KILL $compile_timeout g++ $opts -c $source_file -o $object_file;
        ret=$?;
        linker_to_use="g++"
    fi
    if [ "$ret" -ne "0" ]; then
        break
    fi
    # Add object_file to the array to_link
    to_link+=($object_file)
done

if [ "$ret" -eq "0" ]; then
    # Link all object files
    echo -e "Linking with $linker_to_use";
    timeout --signal=KILL $compile_timeout $linker_to_use -o $executable ${to_link[@]}
    ret=$?;
fi

# Remove all files in $scratch_path except $executable
keepit=$(basename $executable)
find . ! -name "$keepit" -type f -exec rm -f {} \;

if [ "$count" -eq "0" ]; then
    echo "No C/C++ source files found!"
fi

# Check if program compiled successfully
if [ "$ret" -eq "0" ]; then
    echo -e "COMPILE_SUCCESS"
else
    echo -e "COMPILE_FAIL"
fi
