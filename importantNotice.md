# IMPORTANT NOTICE #

In the need to upload and analyze several hundreds files, I made a change in the library.
From now on, the ENTrack objects retrieved are saved to disk with the name md5OfTheFile.enp5 under /Library/Application Support/Processing/EchoNest/ on Mac OS X. I couldn't test this feature under other OSes but I guess you should not see any change. If you're on Mac and want to use this feature, just create the folder above. If you don't create the folder (or are on another OS), you'll have a FileNotFoundException. It looks ugly on the console but shouldn't cause any problem with your code.

The useful thing about this new "feature" is that as the analysis are already parsed and saved to disk, it's a whole lot faster to use with files you already analyzed. First time you send a file, it checks if the corresponding .enp5 file exists on disk, if yes it loads it as a ENTrack object an returns it as usual. If the file doesn't exist, it works as always : send the md5 to the server, if analysis doesn't exists on the server, uploads the file then gets back the analysis, parses it, create the ENTrack object, returns the ENTrack object and saves it as a new .enp5 file so that next time, it won't even need to go online.

If you find this useful (or are just tired of seeing those red messages) I'll take some time to make this more workable, but for the time being, I'm sorry, I'll have to leave it as is.

melka