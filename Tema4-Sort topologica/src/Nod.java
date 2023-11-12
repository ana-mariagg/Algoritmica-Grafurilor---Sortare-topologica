import java.awt.*;
import java.util.Vector;

public class Nod {

    private int coordX;

    private int coordY;

    private int number;

    //predecesorii
    public Nod predecesor=null;
    private Vector<Integer> listaAdiacenta;

    public Nod(int coordX, int coordY, int number)
    {
        this.coordX=coordX;
        this.coordY=coordY;
        this.number=number;
        listaAdiacenta = new Vector<Integer>();
    }

    public int getCoordX() {
        return coordX;
    }

    public Vector<Integer> getListaAdiacenta(){return listaAdiacenta;}

    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void drawNod(Graphics g,int Nod_diam)
    {
        g.setColor(Color.RED);
        g.fillOval(coordX,coordY,Nod_diam,Nod_diam);

        g.setFont(new Font("TimesRoman",Font.BOLD,13));
        g.setColor(Color.BLACK);
        g.drawOval(coordX,coordY,Nod_diam,Nod_diam);
        if(number<10)
            g.drawString(((Integer)number).toString(),coordX+13,coordY+20);
        else
            g.drawString(((Integer)number).toString(),coordX+8,coordY+20);


    }

    public void drawConnectedComponentNod(Graphics g,int Nod_diam,int nr)
    {
        switch (nr) {
            case 0: {
                g.setColor(Color.RED);
                g.fillOval(coordX, coordY, Nod_diam, Nod_diam);
                break;
            }

            case 1: {
                g.setColor(Color.GREEN);
                g.fillOval(coordX, coordY, Nod_diam, Nod_diam);
                break;
            }

            case 2: {
                g.setColor(Color.BLUE);
                g.fillOval(coordX, coordY, Nod_diam, Nod_diam);
                break;
            }
            case 3: {
                g.setColor(Color.CYAN);
                g.fillOval(coordX, coordY, Nod_diam, Nod_diam);
                break;
            }
            case 4: {
                g.setColor(Color.MAGENTA);
                g.fillOval(coordX, coordY, Nod_diam, Nod_diam);
                break;
            }
            case 5: {
                g.setColor(Color.YELLOW);
                g.fillOval(coordX, coordY, Nod_diam, Nod_diam);
                break;
            }
            case 6: {
                g.setColor(Color.PINK);
                g.fillOval(coordX, coordY, Nod_diam, Nod_diam);
                break;
            }
            case 7: {
                g.setColor(Color.ORANGE);
                g.fillOval(coordX, coordY, Nod_diam, Nod_diam);
                break;
            }
            case 8: {
                g.setColor(Color.green);
                g.fillOval(coordX, coordY, Nod_diam, Nod_diam);
                break;
            }
        }
        g.setFont(new Font("TimesRoman",Font.BOLD,13));
        g.setColor(Color.BLACK);
        g.drawOval(coordX,coordY,Nod_diam,Nod_diam);
        if(number<10)
            g.drawString(((Integer)number).toString(),coordX+13,coordY+20);
        else
            g.drawString(((Integer)number).toString(),coordX+8,coordY+20);


    }


}
