public class SyntaxErrorException extends Exception {
    private String info;

    public SyntaxErrorException(String info) {
        this.info = info;
    }
}
