# FitX Android App #

### Technologies Requirements ###

#### Windows Installation: ####
    - Download the latest version of Android Studio
    - If you downloaded a .zip file, unpack the .zip and copy the android-studio folder into your
      Program Files folder
    - Then open the android-studio/bin folder and launch studio64.exe (for 64-bit machines) or 
      studio.exe (for 32-bit machines)
    - Follow the setup wizard in Android Studio and install any SDK packages that it recommends
#### Linux Installation: ####
    - Download the latest version of Android Studio
    - Unpack the .zip file you downloaded to an appropriate location for your applications, such 
      as within /usr/local/ for your user profile, or /opt/ for shared users
    - If you are running a 64-bit version of Ubuntu, you need to install some 32-bit libraries with 
      the following command:
        $ sudo apt-get install libc6:i386 libncurses5:i386 libstdc++6:i386 lib32z1 libbz2-1.0:i386
    - If you are running 64-bit Fedora, the command is:
        $ sudo yum install zlib.i686 ncurses-libs.i686 bzip2-libs.i686
        
### Setup ###
    - N/A
### Running ###
    -
    -
    -
### Deployment ###
    - Our user database will be supported by a google firebase backend. This firebase backend will 
      allow for easy internet redeployment     if necessary
#### Phone Installation ####
    - To run FitX on a mobile device a user must connect via  USB cable and click "Run" on Android 
      studio. For this to happen the users phone must be in Developer mode
    
### Testing ###
    - Before Testing Note: These were recorded with espresso test recorder in Android Studio. Espresso
      from our experience with it has not been good. It is really slow, laggy, and takes a large toll
      on your CPU. So recording tests is very difficult with this. Also the test run and fail with the same 
      exception each time. (android.test.espresso.nomatchingviewexception) I have looked into this exception
      and there is no clear answer to how to fix it. 
    - To run tests: Go to test in project directory, right click it and hit run.
    - Behavioral Tests Location: FitX\app\src\androidTest\java\com\example\fitx\
    - Unit Tests Location: FitX\app\src\test\java\com\example\fitx\

### Authors ###
    - Brett Noltkamper, brettn@email.sc.edu
    - Everett Bishop, ebassman77@gmail.com
    - Vishwajeet Singh, vs2@email.sc.edu
    - William Simmons, simzwill98@gmail.com
