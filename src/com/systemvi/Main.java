package com.systemvi;

import com.systemvi.examples.applicationtest.ApplicationTest;
import com.systemvi.examples.applicationtest.CameraTest;
import com.systemvi.examples.applicationtest.WindowTest;

public class Main {
    public static void main(String[] args) {

        new CameraTest(3,3,60).run();
    }
}
