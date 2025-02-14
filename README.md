##### To release a new OCR library version : 

1. In jitpack.yaml update the version
    Ex:   - mvn install:install-file $FILE -DgroupId=com.github.sayukth -DartifactId=panchayatseva_aadhaar_ocr -**Dversion=0.5.4** -Dpackaging=aar
2. Then open the terminal and run the command ./gradlew assembleRelease 
3. Go to aadhaar-ocr library -> build ->outputs -> aar 
4. In aar we will have the generated file 
5. Copy the generated file and paste in the project root folder which will override the previous file if exists otherwise a new file will be added to root directory
6. Commit the changed files 
7. Go to github website and navigate to the project repository then go to the code section and click on releases Click on Draft a new release by giving a tag as version name 
8. Select the target branch i.e the branch with the changes 
9. Copy the aar file and paste in the release 
10. Click on publish release 
11. Then go to website jitpack.io 
12. Enter the repository link in jitpack.io i.e. get from github 
13. Click on Get it near the current released version 
14. In the opened file in Build artifacts you will get the dependency with latest version which you can add in build.gradle file
