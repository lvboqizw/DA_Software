package com.example.remotecontrol;

import java.util.Random;

public class Trigger {
    private final int TEST_ROUNDS = 10;

    private String ringNr[] = new String [TEST_ROUNDS];
    private String[] triggerList = new String[TEST_ROUNDS];
    private static final Random random = new Random();


    public void generateTriggerList(String mode) {
        switch (mode) {
            case "Test":
                modeTest();
                break;
            case "I":
                modeOne();
                break;
            case "II":
                modeTwo();
                break;
            case "III":
                modeThree();
                break;
            case "IV":
                modeFour();
                break;
            default:
                break;

        }
    }

    public String getNodes(int round) {
        return ringNr[round] + "/" + triggerList[round];
    }

    /**
     * Test Functionality
     * */
    private void modeTest() {
        for (int i = 0; i < TEST_ROUNDS; i ++) {
            ringNr[i] = "1";
            triggerList[i] = "1";
        }
    }

    /**
     * Random single point on the wrist one
     * */
    private void modeOne() {
        for (int i = 0; i < TEST_ROUNDS; i ++) {
            ringNr[i] = "1";
            StringBuilder nodes = new StringBuilder();
            int res = 0b00000000;
            int node = random.nextInt(6);
            res = res | (1 << node);
            nodes.append(res);
            triggerList[i] = nodes.toString();
        }
    }

    /**
     * Random multi points on the wrist one
     * */
    private void modeTwo() {
        for (int i = 0; i < TEST_ROUNDS; i ++) {
            ringNr[i] = "1";

            int nodeNum = random.nextInt(5) + 2;
            StringBuilder nodes = new StringBuilder();
            int res = 0b00000000;
            for (int j = 0; j < nodeNum; j ++) {
                int node = random.nextInt(6);
                res = res | (1 << node);
            }
            nodes.append(res);
            triggerList[i] = nodes.toString();
        }
    }

    /**
     * Single or two same points on all wrist
     * */
    private void modeThree() {
        for (int i = 0; i < TEST_ROUNDS; i ++) {
            ringNr[i] = "7";

            StringBuilder nodes = new StringBuilder();
            int nodeNum = random.nextInt(2) + 1;
            int res = 0b00000000;
            for (int j = 0; j < nodeNum; j ++) {
                int node = random.nextInt(6);
                res = res | (1 << node);
            }
            nodes.append(res);
            triggerList[i] = nodes.toString();
        }
    }

    /**
     * Multi random points on multi random wrist
     * */
    private void modeFour() {
        for (int i = 0; i < TEST_ROUNDS; i ++) {
            int wristNr = random.nextInt(3) + 1;
            StringBuilder wrists = new StringBuilder();
            int wrist_res = 0b00000000;
            for (int j = 0; j < wristNr; j ++) {
                int wrist = random.nextInt(3);
                wrist_res = wrist_res | (1 << wrist);
            }
            wrists.append(wrist_res);
            ringNr[i] = wrists.toString();

            int nodeNum = random.nextInt(6) + 1;
            StringBuilder nodes = new StringBuilder();
            int res = 0b00000000;
            for (int j = 0; j < nodeNum; j ++) {
                int node = random.nextInt(6);
                res = res | (1 << node);
            }
            nodes.append(res);
            triggerList[i] = nodes.toString();
        }
    }
}
