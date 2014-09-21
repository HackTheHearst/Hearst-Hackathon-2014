Live site http://hackhearst.appspot.com

How to build and deploy for AppEngine
====================

1.  Download and install ![Google Plugin for Eclipse][https://developers.google.com/eclipse/docs/getting_started]

2.	Clone this project using git.

3.	Import project into Eclipse.

4.  In Translator.java update the following tokens and keys:
		
		WIT_ACCESS_TOKEN = "See https://wit.ai/";
		HEARST_APP_ID = "See http://hackthehearst.berkeley.edu/api.html";
		HEARST_APP_KEY = "See http://hackthehearst.berkeley.edu/api.html";

5.	Build project and deploy to Appengine.