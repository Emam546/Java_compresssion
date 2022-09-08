package Tools;


public class progressBar  {
    public String prefix = ""; 
    public String suffix = ""; 
    public int decimals = 1; 
    public int length = 40;
    public String fill = "█";
    public String printEnd = " Complete\r";
    public long total=100;
    public int roundTo=5;
    public int getRoundTo() {
        return roundTo;
    }
    void tick (float iteration){
        String percent = String.valueOf(100 * (iteration / (float)total));
        percent=percent.substring(0,Math.min(roundTo,percent.length()));
        int filledLength = Math.round(length * iteration / total);
        filledLength=Math.max(0,Math.min(filledLength, length));
        String bar = fill.repeat(filledLength)  + "-".repeat(length - filledLength);
        System.out.print("\r"+prefix+"|"+bar+"|"+percent+"%"+suffix+printEnd);
    }
    public static void main(String[] args) {
        progressBar bar=new progressBar();
        for (int i = 0; i < 100; i++) {
            bar.tick(i);
        }
    }
    
}
