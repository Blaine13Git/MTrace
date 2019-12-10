package com.ggj.tester;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;

public class AttachAgent {

    public static void main(String[] args) {

        /*
        javac AttachAgent.java
        java AttachAgent pid agentFilePath
         */
        try {
            VirtualMachine vm = VirtualMachine.attach(args[1]); //pid
            vm.loadAgent(args[2]); //agentFilePath
        } catch (AttachNotSupportedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AgentLoadException e) {
            e.printStackTrace();
        } catch (AgentInitializationException e) {
            e.printStackTrace();
        }
    }

}
