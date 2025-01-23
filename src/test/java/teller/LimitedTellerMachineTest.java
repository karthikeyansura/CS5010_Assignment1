package teller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.Before;

/**
 * Test class for the LimitedTellerMachine.
 * This class contains unit tests to verify the functionality of depositing and withdrawing money.
 */
public class LimitedTellerMachineTest {

  private LimitedTellerMachine atm;

  /**
   * Sets up a new instance of the LimitedTellerMachine before each test.
   */
  @Before
  public void setUp() {
    atm = new LimitedTellerMachine();
  }

  /**
   * Tests withdrawing money by making up for shortage with the largest denomination.
   */
  @Test
  public void testWithdrawWithChange() {
    atm = new LimitedTellerMachine();
    atm.deposit(1, 3, 5, 0, 10, 1, 20, 2);
    assertTrue(atm.withdraw(1, 5, 10, 1));
    assertEquals(3, atm.getQuantity(1));
    assertEquals(1, atm.getQuantity(5));
    assertEquals(1, atm.getQuantity(10));
    assertEquals(1, atm.getQuantity(20));
  }

  /**
   * Test case where an exact match of denominations is withdrawn.
   * This ensures that the ATM can handle withdrawal of exactly the available funds.
   */
  @Test
  public void testWithdrawExactMatch() {
    atm.deposit(1, 5, 5, 1, 10, 1, 20, 1);
    assertTrue(atm.withdraw(1, 5, 5, 1, 10, 1, 20, 1));
    assertEquals(0, atm.getQuantity(1));
    assertEquals(0, atm.getQuantity(5));
    assertEquals(0, atm.getQuantity(10));
    assertEquals(0, atm.getQuantity(20));
  }

  /**
   * Test case where an unsupported denomination (value 2) is deposited.
   * This should throw an IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testDepositUnsupportedDenomination() {
    atm.deposit(2, 5);
  }

  /**
   * Test case for withdrawing all available denominations from the ATM.
   * This ensures that the withdrawal works when all funds are requested.
   */
  @Test
  public void testWithdrawAllAvailableMoney() {
    atm.deposit(1, 10, 5, 5, 10, 2, 20, 1);
    assertTrue(atm.withdraw(1, 10, 5, 5, 10, 2, 20, 1));
    assertEquals(0, atm.getQuantity(1));
    assertEquals(0, atm.getQuantity(5));
    assertEquals(0, atm.getQuantity(10));
    assertEquals(0, atm.getQuantity(20));
  }

  /**
   * Test case where there is insufficient total funds to complete the withdrawal.
   * This ensures the ATM rejects withdrawals when it cannot fulfill the request due to insufficient funds.
   */
  @Test
  public void testWithdrawInsufficientFunds() {
    atm.deposit(1, 5, 5, 5, 10, 1, 20, 0);
    assertFalse(atm.withdraw(1, 10, 5, 5, 10, 2));
  }

  /**
   * Test case to check the correct handling of non-sequential deposit and withdrawal actions.
   * This ensures that the order of operations does not affect the correctness of the ATM's behavior.
   */
  @Test
  public void testNonSequentialDepositAndWithdraw() {
    atm.deposit(10, 2, 1, 10, 5, 3, 20, 1);
    assertTrue(atm.withdraw(5, 3, 1, 10, 10, 1, 20, 1));
    assertEquals(0, atm.getQuantity(1));
    assertEquals(0, atm.getQuantity(5));
    assertEquals(1, atm.getQuantity(10));
    assertEquals(0, atm.getQuantity(20));
  }

  /**
   * Tests verifying the updated quantities after 2 deposits and 1 withdraw action.
   */
  @Test
  public void testDepositTwice() {
    atm = new LimitedTellerMachine();
    atm.deposit(1, 3, 5, 0, 10, 1, 20, 15);
    assertTrue(atm.withdraw(1, 43, 10, 3));
    assertEquals(0, atm.getQuantity(1));
    assertEquals(0, atm.getQuantity(5));
    assertEquals(0, atm.getQuantity(10));
    assertEquals(12, atm.getQuantity(20));
    atm.deposit(1, 3, 5, 0, 10, 1, 20, 15);
    assertEquals(3, atm.getQuantity(1));
    assertEquals(0, atm.getQuantity(5));
    assertEquals(1, atm.getQuantity(10));
    assertEquals(27, atm.getQuantity(20));
  }

  /**
   * Tests the initial quantities of the ATM to ensure they are zero.
   */
  @Test
  public void testInitialQuantities() {
    atm = new LimitedTellerMachine();
    assertEquals(0, atm.getQuantity(1));
    assertEquals(0, atm.getQuantity(5));
    assertEquals(0, atm.getQuantity(10));
    assertEquals(0, atm.getQuantity(20));
  }

  /**
   * Tests depositing with an odd number of arguments, which should throw an exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidDepositOddNumberOfArguments() {
    atm.deposit(1, 10, 5);
  }

  /**
   * Tests depositing some denominations and then making a null withdrawal request.
   */
  @Test
  public void testDepositAndWithdrawNullRequest() {
    atm.deposit(1, 10, 5, 5, 10, 2, 20, 1);
    assertTrue(atm.withdraw(null));
    assertEquals(10, atm.getQuantity(1));
    assertEquals(5, atm.getQuantity(5));
    assertEquals(2, atm.getQuantity(10));
    assertEquals(1, atm.getQuantity(20));
  }

  /**
   * Tests depositing a negative quantity, which should throw an exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidDepositNegativeQuantity() {
    atm.deposit(1, -5, 5, 3);
  }
}
