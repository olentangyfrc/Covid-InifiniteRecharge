# Covid-InifiniteRecharge
OZone Robotics - FRC 4611 InfiniteRecharge

*work in progess*

This repository contains code that is shared between applications and robots that are created by FRC Team 4611, Agnes. The repository itself is currently split into two parts: common and robot. The common library contains code that contains useful functions and classes that we use throughout our code. The robot library contains the code for the actual bot. This includes the subsystems, commands, OI, and more.

Installation:

In order to clone this project, visual studio code is needed with WPILib plugin installed. Git and Java must be installed on device. Open terminal, Change the current working directory to the location where you want the cloned directory and type git clone, and then paste: https://github.com/olentangyfrc/Covid-InifiniteRecharge.git (In the Git terminal, [ctrl] + [v] doesnt work, use the [insert] key)

Deploy to the Robot:
Connect your computer to the roborio and open visual studio code. Click top right WPILib logo and search deploy robot code.

Autonomous:
Paths are created in the AutonomousTrajectories file with the field as a coordinate grid and the starting point as the origin (0,0). Each new segment specifies the starting coordinates and ending coordinates of that specific segment. For each segment a degree of rotation can be added as well.

```
Path basicPath = new Path(Rotation2.fromDegrees(___));
 
       basicPath.addSegment(
               new PathLineSegment(
  #Starting Point      new Vector2(0, 0), this should always be 0,0
  #Ending Point        new Vector2(33, 0)
               ),
               Rotation2.fromDegrees(0)
       );
       basicPath.addSegment(
               new PathLineSegment(
                       new Vector2(33, 0),
                       new Vector2(33, 50)
               ),
               Rotation2.fromDegrees(0)
       );
       basicPath.addSegment(
               new PathLineSegment(
                       new Vector2(33, 50),
                       new Vector2(25, 10)
               ),
               Rotation2.fromDegrees(0)
       );
 
```
End Path by:
```
      basicPath.subdivide(SUBDIVIDE_ITERATIONS);
      basicTrajectory = new Trajectory(basicPath, constraints);
```
Make sure to add a getter method for the trajectory add the bottom of the file.
```
   public Trajectory getBasicTrajectory() {
       return basicTrajectory;
   }
```
 

Call on Path by:
           `trajectories.getBasicTrajectory()`


LiDar:
















Vision:

















Raspberry Pi:

Setting up the Network Tables:

```
from networktables import NetworkTables


NetworkTables.initialize(server='10.46.11.2')



sd = NetworkTables.getTable('Vision')


time.sleep(3)
```







Formatting the Video Stream:

```
def gstreamer_pipeline(


   capture_width=1280,


   capture_height=720,


   display_width=1280,


   display_height=720,


   framerate=30,


   flip_method=0,


):


   return (


       "nvarguscamerasrc ! "


       "video/x-raw(memory:NVMM), "


       "width=(int)%d, height=(int)%d, "


       "format=(string)NV12, framerate=(fraction)%d/1 ! "


       "nvvidconv flip-method=%d ! "


       "video/x-raw, width=(int)%d, height=(int)%d, format=(string)BGRx ! "


       "videoconvert ! "


       "video/x-raw, format=(string)BGR ! appsink"


       % (


           capture_width,


           capture_height,


           framerate,


           flip_method,


           display_width,


           display_height,


       )


   )




```
Pushing Data to Network Tables

```
def SendtoNT(____visible, distance, direction):


 sd.putBoolean(‘See___’, ___visible)


 sd.putNumber('____Distance', distance)


 sd.putString('____Direction', direction)


 sd.putString('CoprocessorTime', str(datetime.datetime.now()))




Setting the color range:

print("I am looking for a ____")
__Lower = (20, 110, 110)
__Upper = (30, 255, 255)


Color
HSV Max Value
HSV Min Value
Yellow
30, 255, 255
20,110,110
Green
60, 255, 255
60, 255, 255
Red
179, 255 , 255
161, 155, 84
Blue
126, 255, 255
94, 80, 2
Every Color but White
179, 255, 255
0, 42, 0


```
Getting Camera Input:
```

cap = cv2.VideoCapture(0)


cap.set(3,1280.0)


cap.set(4,720.0)


time.sleep(1)


if not (cap.isOpened()):


   print('Could not open video device')







print("Getting camera image...")

s

OpenCV Filters:


while 1:


   # read the image, change the colors so it's easier to find what we're looking for


   ret, img = scap.read()







      # converts camera input into HSV 


   hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)







   # look for objects of the color range we designated


   mask = cv2.inRange(hsv, __Lower, __Upper)







   # look for shapes in the color we're looking for


   cnts = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL,


                           cv2.CHAIN_APPROX_SIMPLE)







```

Imutils Filter:

```
cnts =imutils.grab_contours(cnts)


center = None
```




Looks for contours and returns a value that can be operated on

Analyzing Footage:

```
If len(cnts) > 0:


      # print(len(cnts))


       c = max(cnts, key=cv2.contourArea)







       # draw a circle on the object we found


       ((x, y), radius) = cv2.minEnclosingCircle(c)


       M = cv2.moments(c)












       if radius > 300:


           print("___ too close")


           SendtoNT(True, .5, center)


       # set minimum size of circle


       elif radius <= 25:


           SendtoNT(False, 0, "unknown")


           print("I cannot see ___.")


       elif radius > 25:


           #print("radius " + str(radius))


           cv2.circle(img, (int(x), int(y)), int(radius),


                      (0, 255, 255), 2)


           cv2.circle(img, center, 5, (0, 0, 255), -1)


           # using the x, y of the center of the circle, is the object to the left, right, or straight ahead from the camera?



           # the numbers are pixels. image is __x__, numbered 0 to 640 on the X axis.


           direction = ''


           if x > __:


               direction = "right"


           elif x < __:


               direction = "left"


           else:


               direction = "center"








```

Find Distance:
```

# approximate distance by measuring radius. The bigger the radius, the closer it is.




distance = round(380/radius, 2)

```

Prints to Console:
```
# print to console what it sees
print("I can see a ball! It is approximately " + str(distance) + " feet away and to the " + direction)
```


Pushes Data to Network Tables:





           # send to ShuffleBoard whether we see the object we're looking for, how far it is and which direction

```
           SendtoNT(True, distance, direction)


           sd.putBoolean('See___', True)


           sd.putNumber('____Distance', distance)


           sd.putString('____Direction', direction)


else:


       print("I cannot see ____")


       SendtoNT(False, 0, "unknown")


       #sd.putString('CoprocessorTime', str(datetime.datetime.now()))


       #sd.putBoolean('See___', False)




```








Shows Camera Input:

```
#input a different filter name to show what the filter is doing
cv2.imshow('img', ____)
k = cv2.waitKey(1)

```







Roborio:

```
public class ChaseBall extends CommandBase {


 private Telemetry telemetry;
 private boolean stop;
 private static Logger logger = Logger.getLogger(ChaseBall.class.getName());



 //private int direction = 0;



 public ChaseBall(Telemetry sqs) {
   // Use addRequirements() here to declare subsystem dependencies.
   telemetry = sqs;
   addRequirements(sqs);
   logger.info("creates ChaseBall");
 }



 // Called when the command is initially scheduled.
 @Override
 public void initialize() {
   logger.info("starts ChaseBall");
   stop = false;



 //stop = true; why is there stop = true?
 }



 // Called every time the scheduler runs while the command is scheduled.
 @Override
 public void execute() {
   SubsystemFactory.getInstance().getDriveTrain().drive(new Translation2d(telemetry.getTranslationalSpeed(), 0), - telemetry.getRotationalSpeed(), true);
   logger.info("going");
   if(telemetry.getBallDistance() <= telemetry.getTargetBallDistance() && telemetry.getBallDirection() == 0)
     stop = true;
   if(!telemetry.getSeeBall())
     SubsystemFactory.getInstance().getDriveTrain().drive(new Translation2d(0, 0), - telemetry.getRotationalSpeed(), true);
   logger.info("checking if at ball");
 }



 // Called once the command ends or is interrupted.
 @Override
 public void end(boolean interrupted) {
   stop = true;
 }



 // Returns true when the command should end.
 @Override
 public boolean isFinished() {
   logger.info("checking if at ball");
   if(telemetry.getBallDistance() <= telemetry.getTargetBallDistance() && telemetry.getBallDirection() == 0)
     return stop;
   else{
     return false;
   }
 }
}
```





Credits
