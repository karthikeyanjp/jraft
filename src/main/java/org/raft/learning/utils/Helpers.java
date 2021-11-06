package org.raft.learning.utils;

import java.util.Random;

public class Helpers {
   private static final Random random = new Random();

   private Helpers() {
   }

   public static int randIntBetween(int min, int max) {
      return random.nextInt(max - min + 1) + min;
   }

   public static Long currentTimeinMillis() {
      return System.currentTimeMillis();
   }

}
