import java.math.BigDecimal;
import java.math.RoundingMode;

public class Player {
    private String id;
    private long deposits; //Needed to later calculate the casino balance.
    private long balance;
    private int number_of_bets;
    private int winning_bets;
    private String info;
    private boolean illegitimate;

    public Player(String id) {
        this.id = id;
        this.illegitimate = false;
        this.winning_bets = 0;
        this.number_of_bets = 0;
        this.balance = 0;
        this.info = "";
    }

    public boolean isIllegitimate() {
        return illegitimate;
    }

    public void setIllegitimate(boolean illegitimate) {
        this.illegitimate = illegitimate;
    }

    public String getId() {
        return id;
    }

    public long getDeposits() {
        return deposits;
    }

    public void setDeposits(long deposits) {
        this.deposits = deposits;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public int getNumber_of_bets() {
        return number_of_bets;
    }

    public void setNumber_of_bets(int number_of_bets) {
        this.number_of_bets = number_of_bets;
    }

    public int getWinning_bets() {
        return winning_bets;
    }

    public void setWinning_bets(int winning_bets) {
        this.winning_bets = winning_bets;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        if (this.getInfo().equals("")) {
            BigDecimal win_percentage = BigDecimal.valueOf(this.winning_bets);
            try {
                win_percentage = win_percentage.divide(BigDecimal.valueOf(this.getNumber_of_bets()), 2, RoundingMode.HALF_UP);
            } catch (ArithmeticException e) {
                return this.getId() + " " + this.getBalance();
            }

            return this.getId() + " " + this.getBalance() + " " + win_percentage;
        }

        return this.info;
    }

    /**
     * Deposits the amount into the balance
     *
     * @param amount Deposited amount
     */
    public void handleDeposit(int amount) {
        this.setBalance(this.getBalance() + amount);
        this.setDeposits(this.getDeposits() + amount);
    }

    /**
     * Checks if it is a legal action, then subtracts the amount from the player's balance.
     *
     * @param amount Withdrawal amount
     * @return false if illegal withdrawal, true if legal
     */
    public boolean handleWithdraw(long amount) {
        if (this.getBalance() - amount < 0) {
            return false;
        }
        this.setBalance(this.getBalance() - amount);
        this.setDeposits(this.getDeposits() - amount);
        return true;
    }

    /**
     * Checks if it is a legal bet, then adds/subtracts the amount from the player's balance depending on the bet.
     *
     * @param amount      Bet amount
     * @param match_info  Data about the match being bet on
     * @param chosen_team Chosen team for this bet
     * @return false if illegal bet, true if legal
     */
    public boolean handleBet(int amount, String[] match_info, String chosen_team) {
        if (this.getBalance() - amount < 0) {
            return false;
        }
        this.setBalance(this.getBalance() - amount);
        this.setNumber_of_bets(this.getNumber_of_bets() + 1);
        if (match_info[1].equals(chosen_team)) {
            this.setWinning_bets(this.getWinning_bets() + 1);
            this.setBalance((int) (this.getBalance() + amount + (amount * (Double.parseDouble(match_info[0])))));
        } else if (match_info[1].equals("DRAW")) {
            this.setBalance(this.getBalance() + amount);
        }


        return true;
    }


}
