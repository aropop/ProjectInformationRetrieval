#! /bin/bash
COLOR='\033[1;34m'
NC='\033[0;37m'
function pause(){
    echo ""
    echo ""
    echo -e "${COLOR}$1${NC}"
    read key
    if [ "$key" != "n" ]
    then
         eval $1
    fi
    read
}
# Pre
sed -i -e 's/public final static float IDF_TRESHOLD = 1;/public final static float IDF_TRESHOLD = 4;/g' src/informationRetrieval/Main.java
mkdir -p binHighIDF
javac -d binHighIDF -cp "lib/*" src/informationRetrieval/*.java
sed -i -e 's/public final static float IDF_TRESHOLD = 4;/public final static float IDF_TRESHOLD = 1;/g' src/informationRetrieval/Main.java
javac -d bin -cp "lib/*" src/informationRetrieval/*.java
pause 'java -cp "bin/:lib/*" informationRetrieval.Main index articles/ index/'
pause 'java -cp "bin/:lib/*" informationRetrieval.Main search "test" index/ | head -20'

# Aspect 1 (boolean)
pause 'java -cp "bin/:lib/*" informationRetrieval.Main search "car AND NOT bike" index/ | head -20'
# Aspect 2 (soundex)
pause 'java -cp "bin/:lib/*" informationRetrieval.Main index articles/ indexSoundex/ true'
pause 'java -cp "bin/:lib/*" informationRetrieval.Main search "herman" indexSoundex/ true | head -20'
pause 'java -cp "bin/:lib/*" informationRetrieval.Main search "hermann" indexSoundex/ true | head -20'
# Aspect 3
pause 'java -cp "bin/:lib/*" informationRetrieval.Main searchBtree "car*" "index/"'
pause 'java -cp "bin/:lib/*" informationRetrieval.Main searchBtree "*car" "index/"'
pause 'java -cp "bin/:lib/*" informationRetrieval.Main searchBtree "c*r" "index/"'
# Aspect 4, 5 (IDF)
pause 'java -cp "binHighIDF/:lib/*" informationRetrieval.Main search "bike AND because" index/ | head -20'
pause 'java -cp "bin/:lib/*" informationRetrieval.Main search "bike AND because" index/ | head -20'

# Aspect 6 (XML)
pause 'java -cp "bin/:lib/*" informationRetrieval.Main XML XML/pages/ indexXML/'
pause 'java -cp "bin/:lib/*" informationRetrieval.Main searchXML "writer/country#france" indexXML/'
pause 'java -cp "bin/:lib/*" informationRetrieval.Main searchXML "writer/country#belgium" indexXML/'
pause 'java -cp "bin/:lib/*" informationRetrieval.Main searchXML "football_player/country#belgium" indexXML/'
pause 'java -cp "bin/:lib/*" informationRetrieval.Main searchXML "#belgium" indexXML/'
