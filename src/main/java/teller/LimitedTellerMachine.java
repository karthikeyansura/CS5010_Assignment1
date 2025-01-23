package teller;

import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;

/**
 * TellerMachine implementation supports only denominations 1, 5, 10, and 20.
 * LimitedTellerMachine implements TellerMachine it adjusts the shortage of requested notes if
 * there are any.
 */
public class LimitedTellerMachine implements TellerMachine {

  // Supported denominations in ascending (1->5->10->20).
  private static final int[] SuppDen = {1, 5, 10, 20};

  private final Map<Integer, Integer> notes;

  /**
   * Initialize the hashmap 'notes' to be empty.
   */
  public LimitedTellerMachine() {
    notes = new HashMap<>();
    for (int d : SuppDen) {
      notes.put(d, 0);
    }
  }

  /**
   * Deposit the specified pairs of (denomination, quantity).
   * @param deposit an even number of integers.
   * @throws IllegalArgumentException if the number of parameters is odd,
   *         if any denomination is unsupported, or if any quantity is negative.
   */
  @Override
  public void deposit(int... deposit) throws IllegalArgumentException {
    // Checks if the deposit is null, if yes it returns nothing, so no change in machine
    if (deposit == null || deposit.length == 0) {
      return; // No action
    }

    // Checks if the deposit has pairs, if not throws exception
    if (deposit.length % 2 != 0) {
      throw new IllegalArgumentException("Deposit arguments must be in pairs");
    }

    // Checks if the deposit ia a supported denomination, if yes, it updates the balance
    for (int i = 0; i < deposit.length; i += 2) {
      int denom = deposit[i];
      int qty   = deposit[i + 1];
      if (isUnSupportedDenomination(denom)) {
        throw new IllegalArgumentException("Unsupported denomination");
      }
      if (qty < 0) {
        throw new IllegalArgumentException("Cannot deposit a negative quantity");
      }
      notes.put(denom, notes.get(denom) + qty);
    }
  }

  /**
   * Withdraw the specified pair of (denomination, quantity) from this machine.
   * Uses the bigger notes to make up for the shortage of the requested notes.
   *
   * @param request an even number of integers.
   * @return true if withdrawal is successful, false if it has failed.
   */
  @Override
  public boolean withdraw(int... request) {
    // Checks if the request is null, so no change in machine
    if (request == null || request.length == 0) {
      return true; // No action needed
    }

    // Checks if the deposit has pairs, if not return false
    if (request.length % 2 != 0) {
      return false;
    }

    // Requested denomination and their quantities is loaded into a hashmap
    Map<Integer, Integer> requestMap = new HashMap<>();
    long ttlReq = 0;
    for (int i = 0; i < request.length; i += 2) {
      int den = request[i];
      int qty   = request[i + 1];

      // Validates if the request is valid, if yes it continues the loading, if not returns false
      if (isUnSupportedDenomination(den) || qty < 0) {
        return false;
      }
      requestMap.put(den, requestMap.getOrDefault(den, 0) + qty);
      ttlReq += (long) den * qty;
    }

    // Check if enough total money is present
    if (ttlReq > getTotalInTeller()) {
      return false;
    }

    // Process denominations from largest to smallest
    int[] descendingRequested = requestMap.keySet().stream()
            .sorted(Comparator.reverseOrder())
            .mapToInt(Integer::intValue)
            .toArray();

    // Fulfill each requested denomination
    for (int denom : descendingRequested) {
      int needed = requestMap.get(denom);
      // If we need zero, skip
      if (needed == 0) {
        continue;
      }

      // If enough notes of requested denomination are not present, produce them
      if (notes.get(denom) < needed) {
        boolean success = produceDenomination(denom, needed);
        if (!success) {
          return false; // Cannot fulfill
        }
      }

      // Check again if enough denominations are present
      if (notes.get(denom) < needed) {
        return false; // Still not enough
      }
      // Remove the requested quantity
      notes.put(denom, notes.get(denom) - needed);
    }
    return true;
  }

  /**
   * Checks for numbers of denominations present
   * @return number of denominations we have of that particular denomination
   *         if the denomination is not supported, returns 0.
   */
  @Override
  public int getQuantity(int denomination) {
    if (isUnSupportedDenomination(denomination)) {
      return 0;
    }
    return notes.get(denomination);
  }

  /**
   * Checks whether a given denomination is supported by the teller machine.
   * @param denom The denomination to check.
   * @return true if the denomination is supported (1, 5, 10, or 20), false otherwise.
   */
  private boolean isUnSupportedDenomination(int denom) {
    return denom != 1 && denom != 5 && denom != 10 && denom != 20;
  }

  /**
   * Calculates the total amount of money currently available in the machine.
   * @return total value of all notes in the machine.
   */
  private long getTotalInTeller() {
    long total = 0;
    for (int d : SuppDen) {
      total += (long) d * notes.get(d);
    }
    return total;
  }

  /**
   * Produce enough notes of 'denom' so that we have at least 'targetTotal' in stock,
   * by breaking bigger denominations one at a time in a stepwise manner.
   * @return true if produced.
   */
  private boolean produceDenomination(int denom, int targetTotal) {
    // Repeatedly break bigger notes until we have at least 'targetTotal' of 'denom'
    while (notes.get(denom) < targetTotal) {
      boolean broken = breakOneFromNextBigger(denom);
      if (!broken) {
        return false;
      }
    }
    return true;
  }

  /**
   * Break exactly ONE note from the next bigger denomination, if available.
   * If that bigger denom is not available, recursively try to produce it from an even bigger one.
   * @return true if we can make up for shortage from next largest denomination.
   */
  private boolean breakOneFromNextBigger(int denom) {
    // If denom=20, there's no bigger note
    if (denom == 20) {
      return false;
    }
    // The next bigger denom in 1->5->10->20
    int bigger = getNextBiggerDenomination(denom);

    // If we don't have any of 'bigger', try to produce it from an even bigger denom
    if (notes.get(bigger) == 0) {
      if (!breakOneFromNextBigger(bigger)) {
        return false;
      }
    }

    // Now we should have at least 1 bigger note
    if (notes.get(bigger) <= 0) {
      return false; // Could not produce a bigger note
    }

    // Use one bigger note, break it down exactly one step
    notes.put(bigger, notes.get(bigger) - 1);
    int factor = bigger / denom;
    notes.put(denom, notes.get(denom) + factor);

    return true;
  }

  /**
   * Finds the next biggest denominations.
   * @return the next biggest denominations.
   */
  private int getNextBiggerDenomination(int denom) {
    for (int i = 0; i < SuppDen.length - 1; i++) {
      if (SuppDen[i] == denom) {
        return SuppDen[i + 1];
      }
    }
    return -1; // Not found or denom=20
  }
}