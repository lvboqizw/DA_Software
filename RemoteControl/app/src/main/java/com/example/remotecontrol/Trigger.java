package com.example.remotecontrol;

import java.util.Random;

public class Trigger {
    private final int TEST_ROUNDS = 10;

    private String ringNr[] = new String [TEST_ROUNDS];
    private String[] triggerList = new String[TEST_ROUNDS];
    private final Random random = new Random();

    public String[] getTriggerList() {
        return triggerList;
    }

    public void generateTriggerList(String mode) {
        switch (mode) {
            case "Test":
                ringNr[0] = "1";
                triggerList[0] = "1";
                break;
            case "I":
                modeOne();
                break;
            case "II":
                ringNr[0] = "1";
                triggerList[0] = "2356";
                break;
            case "III":
                ringNr[0] = "2";
                triggerList[0] = "1";
                break;
            case "IV":
                ringNr[0] = "12";
                triggerList[0] = "123456";
                break;
            default:
                break;

        }
    }

    public String getNodes() {
        return triggerList[0];
    }

    private void modeOne() {
        ringNr[0] = "1";

        for (int i = 0; i < TEST_ROUNDS; i ++) {
            StringBuilder nodes = new StringBuilder();
            int node = random.nextInt(6) + 1;
            nodes.append(node);
            triggerList[i] = nodes.toString();
        }
    }

    private void modeTwo() {
        ringNr[0] = "1";

        int len = random.nextInt(5) + 2;
        for (int i = 0; i < TEST_ROUNDS; i ++) {
            StringBuilder nodes = new StringBuilder();
            int node = random.nextInt(6) + 1;
            for (int j = 0; j < len; j ++) {
                nodes.append(node);
                node += 1;
            }
            triggerList[i] = nodes.toString();
        }
    }

    private void modeThree() {

    }
}
