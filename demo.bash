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
pause 'java -cp "bin/:lib/*" informationRetrieval.Main index articles/ index/'
pause 'java -cp "bin/:lib/*" informationRetrieval.Main search "test" index/ | head -20'

# Aspect 1 (boolean)
pause 'java -cp "bin/:lib/*" informationRetrieval.Main search "car AND NOT bike" index/ | head -20'
# Aspect 2 (soundex)
pause 'java -cp "bin/:lib/*" informationRetrieval.Main index articles/ indexSoundex/ true'
pause 'java -cp "bin/:lib/*" informationRetrieval.Main search "herman" indexSoundex/ true | head -20'
pause 'java -cp "bin/:lib/*" informationRetrieval.Main search "hermann" indexSoundex/ true | head -20'

# Aspect 4, 5 (IDF)
pause 'java -cp "binHighIDF/:lib/*" informationRetrieval.Main search "bike AND because" index/ | head -20'
pause 'java -cp "bin/:lib/*" informationRetrieval.Main search "bike AND because" index/ | head -20'

