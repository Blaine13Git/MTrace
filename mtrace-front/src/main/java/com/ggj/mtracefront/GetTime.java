package com.ggj.mtracefront;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class GetTime {

    static class CPU {
        int time;
        CPU() {
            time = 0;
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int m = in.nextInt();
        int n = in.nextInt();
        int[] task = new int[n];
        for (int i = 0; i < n; i++) {
            task[i] = in.nextInt();
        }

        in.close();
        ArrayList<CPU> cpu = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            cpu.add(new CPU());
        }
        Arrays.sort(task);

        int total = 0;
        int index = 0;

        while (true) {
            int i = getZero(cpu);
            if (i == 101) {
                break;
            }

            //////////////////任务执行/////////////////////////
            if (i >= 0) {
                if (index == n) {
                    cpu.remove(i);
                } else {
                    cpu.get(i).time = task[index];
                    index++;
                }
            } else {
                total -= i;
            }
        }
        System.out.print(total);
    }

    static int getZero(ArrayList<CPU> cpu) {
        if (cpu.size() == 0) {
            return 101;
        }

        int min = 100;

        for (int i = 0; i < cpu.size(); i++) {
            if (cpu.get(i).time == 0) {
                return i;
            }
            min = min > cpu.get(i).time ? cpu.get(i).time : min;
        }

        for (int i = 0; i < cpu.size(); i++) {
            cpu.get(i).time -= min;
        }
        return -min;
    }
}
