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
    - Our user database will be supported by a google firebase backend. This firebase backend will allow for easy internet redeployment     if necessary
#### Phone Installation ####
    - To run FitX on a mobile device a user must connect via  USB cable and click "Run" on Android 
      studio. For this to happen the users phone must be in Developer mode
    
### Testing ###
    -Upcoming

### Authors ###
    - William Simmons, simzwill98@gmail.com
    - Brett Noltkamper, brettn@email.sc.edu
