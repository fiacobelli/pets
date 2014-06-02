Here are some of my pet projects.

* SimpleArgParse is a java project with ONE class. It is aimed as a simple command line argument parser. Much like argparse for python and Argparse4j. However, it is a lot simpler to use and because it is one class, there's no Maven nor jars to deal with for simple tasks. Just copy the class to your package (working directory) and use it.

* Scripts:
 - beamer2reveal.py: This is a script that will take slides made with LaTeX Beamer and will do a lot of the basic conversions to turn it into an html slide to be displayed using reveal.js. You need to provide the filename of the "tex" slides in beamer. It will create a file named exactly the same with the "html" extension. You will then need to edit that, but a lot of the work will be done for you. Additionally you can pass a parameter "-root" with the location of your reveal.js root. If not passed, it assumes "../www/" I should change that :)
 
