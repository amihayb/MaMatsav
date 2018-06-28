import numpy as np
import cv2
import imutils
import time

from firebase import firebase

fb = firebase.FirebaseApplication('https://turtles1-f2554.firebaseio.com/', None)

cap = cv2.VideoCapture(0)
# initialize the first frame in the video stream
firstFrame = None

ret, frame = cap.read()
time.sleep(5.0)

firstTurtle = True

while(True):
    text = "No"
    # Capture frame-by-frame
    ret, frame = cap.read()

    # Our operations on the frame come here
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    
    # if the first frame is None, initialize it
    if firstFrame is None:
        firstFrame = gray
        continue

    # compute the absolute difference between the current frame and
    # first frame
    frameDelta = cv2.absdiff(firstFrame, gray)
    thresh = cv2.threshold(frameDelta, 25, 255, cv2.THRESH_BINARY)[1]

    firstFrame = gray
    
    # dilate the thresholded image to fill in holes, then find contours
    # on thresholded image
    thresh = cv2.dilate(thresh, None, iterations=2)
    cnts = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL,
                            cv2.CHAIN_APPROX_SIMPLE)
    cnts = cnts[0] if imutils.is_cv2() else cnts[1]

    # loop over the contours
    for c in cnts:
        # if the contour is too small, ignore it
        if cv2.contourArea(c) < 5000:
            continue

        # compute the bounding box for the contour, draw it on the frame,
        # and update the text
        (x, y, w, h) = cv2.boundingRect(c)
        cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
        text = "Yes"
        if (firstTurtle):
            fb.put('/nest1/','isHetched','1') #"path","property_Name",property_Value    
    
    cv2.putText(frame, "Movement: {}".format(text), (10, 200),
                cv2.FONT_HERSHEY_SIMPLEX, 2, (1, 1, 1), lineType=cv2.LINE_AA)
    
    # Display the resulting frame
    cv2.imshow('frame',frame)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
# When everything done, release the capture
cap.release()
cv2.destroyAllWindows()