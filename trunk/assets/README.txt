NCompass - a target compass and place management application

Objectives 
-----------------------------------------------
Overall: make it trivially easy and quick to use place information

Target Compass
* Make places understandable as quickly as possible without a network connection 
* Make it easy (one step) to mark where you are as a place of interest.
* Give a simple picture of where a place of interest is, relative to the current location.

Place Book
* Keep track of places of interest for a variety of purposes 
  (sent them, received them, targeted them, etc)
* Make it easy to reuse those places for new purposes





Some Imagined Uses
-----------------------------------------------
* Find your car in the parking lot
* See how far you are from home
* Get back to your hotel room
* Send your friend a meeting place
* Get back to your campsite
* Send your family your campsite location in case of emergency
* Tell your coworker where your favorite bed and breakfast 
  in Italy is
* Get a sense of direction in a foreign city
* See how far you are from your last climbing summit
* Answer the question "are we there yet" definitively
* See how far away from your family you are
* Check how fast the plane is traveling
* Quickly and quietly send your location in an emergency
* Quickly find your friend in trouble
* Use as a backup for your nautical equipment
* Relocate your favorite fishing hole
* Sight impaired friends may use to find one another in 
  unfamiliar territory (requires audio)
* Find your English class from an unusual parking spot
* Acclimate yourself to the relative places of places on campus



Instructions for Use
-----------------------------------------------
TARGET COMPASS
How to target the current location:
	1. Open Target Compass
	2. Touch and release the compass face
		OR
	2. Touch and release the DPAD Center button
	  	OR
	2. Select the "Target Here" menu option

How to target a previously targeted location
	1. Open the Place Book
	2. navigate to the 'targeted' list
	3. scroll to a place
	4. Press the DPAD center
		OR
	4. Press the "Target Location" menu option

How to map a targeted location
	1. Open the location in the Target Compass
	2. Select the "Map Target" menu option

How to change the title of a location
	1. Open the location in the Target Compass
	2. Select the "Set Target Title" menu option
		OR
	2. Touch the title portion of the compass window
	3. Key in a new title
	4. Press OK

How to send a targeted location to a friend
	1. Open the location in the Target Compass
	2. Select the "Send Target" menu option
	3. Provide a phone number and add any message content
	4. Press OK

How to hear the bearing to target and north:
	1. Open Target Compass
	2. Touch and release the DPAD Left button
Note: this feature is not yet reliable and may 
cause sudden shutdown of the compass. 



Meaning of the Display Options:
Display Mode controls what the compass will look like. Power Saver
draws simple graphics for the compass back and needle, dramatically
reducing cpu load. The three other options draw the standard compass
NSEW background with different needle graphics. 

You can also set the compass display color. This color will be used
for all the on screen elements including text and the NSEW graphics.
You can choose one of the supplied values or use a RGB color value
of the form AARRGGBB or RRGGBB. 


PLACE BOOK
The place book is a list of lists. The gallery on the screen bottom
contains the names of each list. You can add and delete lists using
the menu. 

The items in a list represent places. Each place compacts down to show 
only the time it was last modified and its distance from the current 
location (cached and will only update when switching between lists). 
When expanded, it will reveal its title and picture if they have been 
set. Items will be added automatically to the targeted, sent and 
received lists based on user activity.

When you select a place, two more menu options present a means to 
delete or view that place. Viewing a place means vieing it in the 
Target Compass.


RECEIVING A LOCATION
If the message body of an SMS message contains a geo url, it will 
be captured and displayed in a Target Compass automatically. 
For example, geo:37.44984217102031,-122.12027995021214,1000.0
contained in a message body will suffice.






Future Development
-----------------------------------------------
Audio support of the compass info for visually impaired or pocket use
Better interface for sending location messages to others
Smoother touch screen operation for the place book
Better handling of multiple compass and place book instances
Connect places with contacts and media (photos, audio, video)
Synch a whole place list/book to another person/device. 
Do not interrupt in progress call with an arrived location
Display richer contact information for received places
More options for customizing the display (images, info, layout, etc)
Better integration/connection with map views and data
Construct a sign post style view for simultaneous display of a set of places