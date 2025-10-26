When this procedure returns true, the animation playback will start,
unless the animation is already playing.

When this procedure returns false, the animation playback will stop.

If the toggle happens too fast, there may not be enough time for the animation to start playing
or the playback to restart.

If the animation is not set to loop, it will stop when it reaches the end of the animation
and the condition needs to toggle from true to false and back to true to re-start the animation.

For walking animations, this parameter controls when the animation is being played.