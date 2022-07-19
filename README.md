# java-kernel-image-processing

An image processing application written in Java supporting *sequential*, *parallel* and *distributed* modes of execution. It is achieved by doing a convolution between the kernel and an image. You can read more about this technique [here](https://en.wikipedia.org/wiki/Kernel_(image_processing)).

The **parallel** mode was achieved by using *Thread* class. The **distributred** mode was achieved by using *MPJ Express* library (which is an implementation of MPI in Java), so you shoud install it to your system before you start working with the project *(I used Version 0.44 of MPJ Express, which was the latest one at the moment of publication)*.

For more details, you may read my report about the application [**HERE**]().
