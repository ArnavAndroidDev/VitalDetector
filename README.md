This project can detect heart rate/Blood pressure/ respiration rate and anemia like diseases

This project uses tensorflow to detect disease like anemia.

The App uses the PreviewCallback mechanism to grab the latest image from the preview frame. It then processes the YUV420SP data and pulls out all the red pixel values.

It uses data smoothing in a Integer array to figure out the average red pixel value in the image. Once it figures out the average it determines a heart beat when the average red pixel value in the latest image is greater than the smoothed average.

The App will collect data in ten second chunks and add the beats per minute to another Integer array which is used to smooth the beats per minute data.


