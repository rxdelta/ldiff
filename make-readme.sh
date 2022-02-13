TMP=readme.tmp
SAMPLE=src/main/java/net/rxdelta/ldiff/Sample.java

cat README.md | grep -B 100 '^## simple sample' > $TMP
cat $SAMPLE   | grep -A 100 'private static void sample1()' | tail -n +2 | grep -B 100 -m 1 '^    }' | head -n -1 >> $TMP
echo "" >> $TMP
echo "## deep inspect sample" >> $TMP
cat $SAMPLE   | grep -A 100 'private static void sample2()' | tail -n +2 | grep -B 100 -m 1 '^    }' | head -n -1 >> $TMP
echo "" >> $TMP

#mv README.md README.md.old
mv $TMP README.md
