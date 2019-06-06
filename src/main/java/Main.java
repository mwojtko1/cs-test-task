public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("Exactly one parameter is required.");
        } else {
            Parser parser = new Parser();
            parser.parseFile(args[0]);
        }
    }
}
