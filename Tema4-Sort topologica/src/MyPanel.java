import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.Visibility;
import java.io.*;
import java.util.*;


public class MyPanel extends JPanel {
    private int NodNr = 0;
    static public int NodDiam = 35;
    private Vector<Nod> listaNoduri;
    private Vector<Arc> listaArce;
    Point pointStart = null;
    Point pointEnd = null;
    boolean isDragging = false;
    boolean grafOrientat = false;
    private int[][] matriceAdiacenta = new int[200][200];
    boolean modMutareNoduri = false;
    int indexNodMutat;

    //folosit pentru butornul de componente tare conexe, daca exista, le arata pe ectran, daca nu, afiseaza un mesaj in consola
    boolean bstrongConnectedComponents = false;
    //matricea de componente conexe, pe fiecare linie avem cate un vector cu componentele conexe in parte
    private Vector<Vector<Integer>> strongConnnectedComponentslist;
    //vector cu nodurile comp conexe
    private Vector<Nod> strongConnectedComponentNods;
    //vector ca sa imi creez arce intre componentele conexe
    private Vector<Arc> strongConnnectedComponentslistArcs;

    //nodul de pornire
    Nod start;



    //initializare matrice cu valori de 0
    void initMatrice(int[][] matriceAdiacenta)
    {
        for (int index = 0; index < listaNoduri.size(); index++) {
            for (int index2 = 0; index2 < listaNoduri.size(); index2++) {
                matriceAdiacenta[index][index2] = 0;
            }
        }
    }

    //sterge graful
    void deleteGraf(Vector<Nod> listaNoduri, Vector<Arc> listaArce) {
        listaArce.clear();
        listaNoduri.clear();
        initMatrice(matriceAdiacenta);
        NodNr = 0;
        repaint();
    }

    //daca un nod se afla prea aproape de altul atunci nu se adauga
    boolean canAddNod(int x, int y) {
        for (int i = 0; i < listaNoduri.size(); i++)
            if (Math.sqrt((Math.pow((listaNoduri.elementAt(i).getCoordX() - x), 2)) +
                    (Math.pow((listaNoduri.elementAt(i).getCoordY() - y), 2))) < 40)
                return false;
        return true;
    }

    // functie folosita pt a verifica daca coordonatele mouse-ului se afla pe surpafata unui nod
    boolean NodSurface(int x, int y, Point p) {
        return (((x < p.getX()) && (p.getX() < x + NodDiam)) && ((y < p.getY()) && (p.getY() < y + NodDiam)));
    }


    //matrice
   /* void afisareMatriceAdiacenta(int[][] matriceAdiacenta) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter("MatriceAdiacenta.txt"));
            out.println("In graf sunt " + NodNr + " noduri");
            out.print("\n");
            out.println("Matricea de adiacenta: ");
            for (int index = 0; index < listaNoduri.size(); index++) {
                for (int index2 = 0; index2 < listaNoduri.size(); index2++) {
                    out.print(matriceAdiacenta[index][index2] + " ");
                }
                out.print("\n");
            }
            out.close();
        } catch (IOException e1) {
            System.out.println("Eroare la scrierea in fisierul MatriceAdiacenta.txt");
        }
    }
*/
    //metoda care se apeleaza la eliberarea mouse-ului
    private void addNod(int x, int y) {
        Nod Nod = new Nod(x, y, NodNr);
        listaNoduri.add(Nod);
        NodNr++;
        repaint();
    }

    //,etoda care verifica ca graful sa nu fie ciclic
    private boolean CanTopologicSort()
    {
        Vector<Nod> visited = new Vector<>();
        Vector<Nod> nonvisited = new Vector<>();
        Vector<Arc> arcs = new Vector<>();

    //se adauga nodul in lista de nevizitate
        for(int i=0; i<listaNoduri.size();i++)
        {
            nonvisited.add(listaNoduri.elementAt(i));
        }
        //il eliminam pe start din nevizitate si il bagam in vizitate pentru ca e nodul de inceput
        nonvisited.remove(start);
        visited.add(start);

        //fac o copie a arcelor ca sa nu modific lista de originale pentru ca aici doar verific daca merge
        for(int i=0;i<listaArce.size();i++)
        {
            arcs.add(listaArce.elementAt(i));
        }

        while(nonvisited.size()!=0)
        {
            //cat timp stiva de vizitate nu e goala
            while (visited.size() > 0)
            {
                //ultim=ultimul nod din stiva
                Nod ultim = visited.lastElement();
                //nou=nodul de care se leaga in mod direct "ultim", nodul pe care il gaseste csnd cauta arc
                Nod nou = null;
                for(int j=0;j<arcs.size();j++)
                {
                    //daca gaseste arc intre cel din stia si noul nod, sterge arcul din lista copie ca sa nu iau acelasi arc de doua ori si nou se face el de la capatul arcului
                    if(arcs.elementAt(j).getStart() == ultim) {
                        nou = arcs.elementAt(j).getEnd();
                        Arc arc = arcs.elementAt(j);
                        arcs.remove(arc);
                        break;
                    }
                }
                //daca gaseste un nou si nodul nou este deja vizitat=> are ciclu => NU SE POATE SORTARE TOPOLOGICA
                if(nou != null)
                {
                    if(visited.contains(nou)) return false;
                    //altfel imi muta nodul din vizitate in nevizitate
                    else
                    {
                        nonvisited.remove(nou);
                        visited.add(nou);
                    }
                }
                //daca nu imi gaseste nod nou
                else
                {
                    visited.remove(ultim);
                }
            }
            //daca am golit stiva de vizitate si mai sunt nevizitate, atunci se trece mai departe catre posibila comp conexaa
            if(nonvisited.size()!=0)
            {
                visited.add(nonvisited.firstElement());
                nonvisited.remove(0);
            }

        }

        return true;
    }

    //parcurgerea in adancime
    private Vector<Integer> PTDF()
    {
        Vector<Nod> analised = new Vector<>();
        Vector<Nod> visited = new Vector<>();
        Vector<Nod> nonvisited = new Vector<>();
        Vector<Integer> t1 = new Vector<>();
        Vector<Integer> t2 = new Vector<>();
        Vector<Arc> arcs = new Vector<>();

        //incepe in ordine cresc dupa nr. nod
        start=listaNoduri.firstElement();

        //se face iar copia de arce ca sa nu se strice graful original
        for(int i=0;i<listaArce.size();i++)
        {
            arcs.add(listaArce.elementAt(i));
        }
        //se recreeaza lista de noduri nevizitate si se sterge din el startul
        for(int i=0; i<listaNoduri.size();i++)
        {
            nonvisited.add(listaNoduri.elementAt(i));
        }
        nonvisited.remove(start);
        visited.add(start);

        //facem t1 si t2 infinit
        for(int i=0;i<listaNoduri.size();i++)
        {
            listaNoduri.elementAt(i).predecesor=null;
            t1.add(Integer.MAX_VALUE);
            t2.add(Integer.MAX_VALUE);
        }
        //punem pentru poz nodului de start timpul = 1
        int t=1;
        t1.set(start.getNumber(),t);

        //while-ul mare pseudocod-> atata timp cat avem si vizitate si nevizitate
        while(nonvisited.size() != 0 || visited.size()!=0)
        {

            //cat timp avem vizitate in stiva
            while (visited.size() > 0)
            {
                //ultim=ultimul din stiva si nou=nodul ce se leaga prin arc de ultim
                Nod ultim = visited.lastElement();
                Nod nou = null;
                //pt fiecare nod din lista de arce startul este "ultim", atunci nou=end-ul arcului
                for(int j=0;j<arcs.size();j++)
                {
                    if(arcs.elementAt(j).getStart() == ultim) {
                        nou = arcs.elementAt(j).getEnd();
                        //daca nou e nevizitat se sterge arcul de la ultim la nou
                        if(nonvisited.contains(nou))
                        {
                            Arc arc = arcs.elementAt(j);
                            arcs.remove(arc);
                            break;
                        }
                        else
                        {
                            //daca e arc intre ultim si nou care e deja vizitat se sterge de tot arcul din lista de vizitate cu tot cu ultim si nou
                            Arc arc = arcs.elementAt(j);
                            arcs.remove(arc);
                            j--;
                            nou=null;
                        }
                    }
                }
                //daca dupa verificare avem nod ce se leaga de ultim
                if(nou != null)
                {
                    //stergem noul din nevizitate si il mutam in vizitate si se face ultim predecesorul lui nou
                    nonvisited.remove(nou);
                    visited.add(nou);
                    nou.predecesor=ultim;
                    //creste timpul
                    t++;
                    //se adauga in t1 pe pozitia lui "nou" nr de mutari(t) efectuate pana se ajunge la "nou"
                    t1.set(nou.getNumber(),t);
                }
                //daca nou nu exista atunci se elimina "ultim" din stiva de vizitate si se trece in vect de analizate
                //creste timpul t(nr de pasi) si se seteaza pe poz lui "ultim din t2 timpul t
                else
                {
                    visited.remove(ultim);

                    analised.add(ultim);
                    t++;
                    t2.set(ultim.getNumber(),t);
                }
            }

            //daca vizitatele sunt 0 si nevizitatele inca mai exista => mai sunt posibile comp tare conexe
            //=> se creaza un nou start in continuare in vect de nevizitate, primul din ce a mai ramas
            //se sterge start-ul din nevizitate si se muta in vizitate, timpul t(nr de mutari) creste si se reia
            if(nonvisited.size()!=0)
            {
                visited.add(nonvisited.firstElement());
                nonvisited.remove(0);
                t++;
                t1.set(visited.firstElement().getNumber(),t);
            }

        }
        //afisam timpii
        System.out.println("t1: "+t1);
        System.out.println("t2: "+t2);
        return t2;
    }


    //sortarea topologica->imi rearanjeaza t2 ul desc
    //am o mapa sub forma de arbore cu integer=timpul aferent nodului=Nod
    private SortedMap<Integer,Nod> topologicSort(Vector<Integer> t2)
    {
        SortedMap<Integer,Nod> SortareTopologica = new TreeMap<>(Collections.reverseOrder());

        //pun timpul->val din t2 si nodul aferent
        for(int i=0;i<t2.size();i++)
        {
            SortareTopologica.put(t2.elementAt(i),listaNoduri.elementAt(i));
        }
        //;e aranjeaza in ordine si fara duplicate
        Set s = SortareTopologica.entrySet();
        //iterarea map-ului si punerea in key->timpul si in value nr nodului
        Iterator i = s.iterator();
        while (i.hasNext()) {
            Map.Entry m = (Map.Entry)i.next();

            int key = (Integer)m.getKey();
            Nod value = (Nod)m.getValue();

            System.out.println("Timp : " + key
                    + "  Nod : " + value.getNumber());
        }
        return SortareTopologica;
    }

    //componentele tare-conexe
    private void StrongConnectectedComponents(SortedMap<Integer,Nod> topoSorted,Vector<Integer> t2)
    {
        Vector<Nod> analised = new Vector<>();
        Vector<Nod> visited = new Vector<>();
        Vector<Nod> nonvisited = new Vector<>();
        Vector<Arc> arcs = new Vector<>();
        Vector<Nod> componentNods = new Vector<>();
        //un vector cu nodurile unei comp conexe + ultimele 2 poz in vect = coord x si y ale ultimului nod
        //din comp conexa ca sa fie repr grafic nodul componenta pe poz aia
        strongConnnectedComponentslist = new Vector<>();

        //start-ul e el cu timpul cel mai mare apoi il sterge din mapa
        //se adauga o linie a matr
        start=topoSorted.get(topoSorted.firstKey());
        topoSorted.remove(topoSorted.firstKey());
        componentNods.add(start);

        //se inverseaza arcele intre ele
        for(int i=0;i<listaArce.size();i++)
        {
            Arc arc2 = new Arc(listaArce.elementAt(i).getEnd(),listaArce.elementAt(i).getStart());
            arcs.add(arc2);
        }

        //se fac nevizitatele si se muta start ul din nevizitate in vizitate
        for(int i=0; i<listaNoduri.size();i++)
        {
            nonvisited.add(listaNoduri.elementAt(i));
        }
        nonvisited.remove(start);
        visited.add(start);

        //ptdf
        while(visited.size() > 0 || nonvisited.size()>0)
        {

            while (visited.size() > 0)
            {
                Nod ultim = visited.lastElement();  //ult el din stiva de vizitate
                Nod nou = null;    //nodul care se leaga cu "ultim"
                for(int j=0;j<arcs.size();j++)
                {
                    //daca e arc si nou e nevizitat se elimina nou
                    if(arcs.elementAt(j).getStart() == ultim) {
                        nou = arcs.elementAt(j).getEnd();
                        if(nonvisited.contains(nou)) {
                            Arc arc = arcs.elementAt(j);
                            arcs.remove(arc);
                            break;
                        }
                        else
                        {
                            Arc arc = arcs.elementAt(j);
                            arcs.remove(arc);
                            j--;
                            nou=null;
                        }
                    }
                }
                // daca E nou se muta din U in V, se muta nodul in linia matr de comp conexe
                // si se salveaza predecesorul si se sterge si timpul din mapa sortata
                if(nou != null)
                {
                    nonvisited.remove(nou);
                    visited.add(nou);
                    nou.predecesor=ultim;
                    componentNods.add(nou);
                    topoSorted.remove(t2.elementAt(nou.getNumber()));
                }
                //se face "ultim" analizat
                else
                {
                    visited.remove(ultim);
                    analised.add(ultim);
                }
            }

            //se salv poz ult nod
            Vector<Integer> pozitii=new Vector<>();
            for(int iterator=0;iterator<componentNods.size();iterator++)
            {
                pozitii.add(componentNods.elementAt(iterator).getNumber());
            }
            pozitii.add(componentNods.elementAt(0).getCoordX());
            pozitii.add(componentNods.elementAt(0).getCoordY());
            strongConnnectedComponentslist.add(pozitii);

            //daca mai sunt el in U se verifica posibilele el conexe
            if(nonvisited.size()!=0)
            {
                componentNods.clear();
                start=topoSorted.get(topoSorted.firstKey());
                topoSorted.remove(topoSorted.firstKey());
                componentNods.add(start);
                visited.add(start);
                nonvisited.remove(start);
            }

        }

        //afisez fiecare nod al componentei conexe si la final coordonatele ultimului nod ca sa se af comp conexa acolo
        //pt verificare
        for(int i=0;i<strongConnnectedComponentslist.size();i++) {
            for (int j = 0; j < strongConnnectedComponentslist.elementAt(i).size(); j++)
                System.out.println(strongConnnectedComponentslist.elementAt(i).elementAt(j));
            System.out.println("\n");
        }
    }


    //transforma din matr de comp conexe in noduri pr zise
    void createStrongConnectedComponentsForGraf()
    {
        //crearea nodurilor
        strongConnectedComponentNods=new Vector<>();
        strongConnnectedComponentslistArcs=new Vector<>();
        for(int i=0;i<strongConnnectedComponentslist.size();i++)
        {
            //imi face din ex v={1,2,3,4) s='1234' -> nr de pe nod
            String number="";
            for (int j = 0; j < strongConnnectedComponentslist.elementAt(i).size()-2; j++)
            {
                number= number + strongConnnectedComponentslist.elementAt(i).elementAt(j).toString();
            }
            int nr= Integer.parseInt(number);
            Nod nod=new Nod(strongConnnectedComponentslist.elementAt(i).elementAt(strongConnnectedComponentslist.elementAt(i).size()-2),
                    strongConnnectedComponentslist.elementAt(i).elementAt(strongConnnectedComponentslist.elementAt(i).size()-1),nr);
            strongConnectedComponentNods.add(nod);
        }

        //crearea arcelor
        for(int index=0;index<listaArce.size();index++)
        {
            int poz_i1= -1;     //x-ul
            int poz_i2= -1;     //y-ul
            for (int i = 0; i < strongConnnectedComponentslist.size(); i++) {

                for (int j = 0; j < strongConnnectedComponentslist.elementAt(i).size() - 2; j++) {
                    //daca nr nodului gasit la inceputul arcului in lista de arce = cu nr nodului din linia din matr de comp tare conexe
                    if(listaArce.elementAt(index).getStart().getNumber() == strongConnnectedComponentslist.elementAt(i).elementAt(j))
                        poz_i1=i;
                    //daca nr nodului gasit la sfarsitul arcului in lista de arce = cu nr nodului din linia din matr de comp tare conexe
                    if(listaArce.elementAt(index).getEnd().getNumber() == strongConnnectedComponentslist.elementAt(i).elementAt(j))
                        poz_i2=i;
                    //am comp conexe 123 si 456 si am arc intre 3----> 4 => am arc si intre 123----->456
                }
            }
            if(poz_i2!=poz_i1 && poz_i1!=-1 && poz_i2!=-1) {
                Arc arc = new Arc( strongConnectedComponentNods.elementAt(poz_i1),strongConnectedComponentNods.elementAt(poz_i2));
                strongConnnectedComponentslistArcs.add(arc);
            }
        }



    }

    void deleteStrongConnectedGraf()
    {
        strongConnnectedComponentslist.clear();
        strongConnectedComponentNods.clear();
        strongConnnectedComponentslistArcs.clear();
        repaint();
    }

    //face comp tare-conexe pr zise
    void componenteleTareConexe()
    {
        Vector<Integer> timpi=PTDF();
        SortedMap<Integer,Nod> SortataTopoligic = topologicSort(timpi);
        StrongConnectectedComponents(SortataTopoligic,timpi);
        createStrongConnectedComponentsForGraf();
        deleteGraf(listaNoduri,listaArce);
        repaint();
    }




    public MyPanel() {
        listaNoduri = new Vector<Nod>();
        listaArce = new Vector<Arc>();
        initMatrice(matriceAdiacenta);

        // borderul panel-ului
        setBorder(BorderFactory.createLineBorder(Color.black));
        MyPanel f = this;

        //creez buton pentru modul de desenare graf orientat
        JToggleButton b = new JToggleButton("Graf neorientat");
        b.setBounds(50, 100, 95, 30);
        f.add(b);
        b.setVisible(true);

        //daca butonul b e apasat, se intra in modul de graf orientat
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
                boolean selected = abstractButton.getModel().isSelected();
                if (selected) {
                    grafOrientat = true;
                    b.setText("Graf neorientat");
                    deleteGraf(listaNoduri, listaArce);
                } else {
                    grafOrientat = false;
                    b.setText("Graf orientat");
                    deleteGraf(listaNoduri, listaArce);
                }
            }
        };
        b.addActionListener(actionListener);


        //butonul pt PTDF
        JToggleButton b2 = new JToggleButton("Sortarete Topologica");
        b.setBounds(20, 100, 95, 30);
        f.add(b2);
        b2.setVisible(true);

        ActionListener actionListener2 = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                AbstractButton abstractButton1 = (AbstractButton) actionEvent.getSource();
                boolean selected2 = abstractButton1.getModel().isSelected();
                if (selected2) {
                    if(CanTopologicSort() == false)
                    {
                        System.out.println("Nu se poate efectua sortarea! GRAFUL ESTE CICLIC!");
                    }
                    else {
                        Vector<Integer> times = PTDF();
                        topologicSort(times);
                    }
                }
            }
        };
        b2.addActionListener(actionListener2);

        JToggleButton b3 = new JToggleButton("Componente tare conexe");
        b.setBounds(20, 100, 95, 30);
        f.add(b3);
        b3.setVisible(true);

        ActionListener actionListener3 = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                AbstractButton abstractButton1 = (AbstractButton) actionEvent.getSource();
                boolean selected2 = abstractButton1.getModel().isSelected();
                if (selected2) {
                    bstrongConnectedComponents=true;
                    componenteleTareConexe();
                }
                else {
                    bstrongConnectedComponents=false;
                    deleteStrongConnectedGraf();

                }
            }
        };
        b3.addActionListener(actionListener3);

        addMouseListener(new MouseAdapter() {
            //evenimentul care se produce la apasarea mousse-ului
            public void mousePressed(MouseEvent e) {
                pointStart = e.getPoint();
                if (modMutareNoduri) {
                    for (int i = 0; i < listaNoduri.size(); i++) {
                        if (NodSurface(listaNoduri.elementAt(i).getCoordX(), listaNoduri.elementAt(i).getCoordY(), pointStart)) {
                            indexNodMutat = i;
                        }
                    }
                }
            }

            //evenimentul care se produce la eliberarea mousse-ului
            public void mouseReleased(MouseEvent e) {

                if (!modMutareNoduri) {
                    if (!isDragging) {
                        //adaugam primul nod in lista
                        if (listaNoduri.size() == 0)
                            addNod(e.getX(), e.getY());
                        else
                            //verificam distanta intre punctele curente si fiecare nod din lista
                            if ((canAddNod(e.getX(), e.getY())))
                                addNod(e.getX(), e.getY());

                    } else {

                        //verificam daca pointStart este pe suprafata unui nod
                        for (int i = 0; i < listaNoduri.size(); i++) {
                            if (NodSurface(listaNoduri.elementAt(i).getCoordX(), listaNoduri.elementAt(i).getCoordY(), pointStart)) {

                                //verificam daca pointEnd este pe suprafata unui nod
                                for (int j = 0; j < listaNoduri.size(); j++) {
                                    if (NodSurface(listaNoduri.elementAt(j).getCoordX(), listaNoduri.elementAt(j).getCoordY(), pointEnd)) {

                                        //daca ambele conditii sunt indeplinite si nodurile sunt diferite, se adauga arcul in listaArce
                                        if (i != j) {
                                            Arc arc = new Arc(listaNoduri.elementAt(i),listaNoduri.elementAt(j));
                                            listaArce.add(arc);

                                            if (grafOrientat) {
                                                //se adauga arcul in matricea de adiacenta
                                                matriceAdiacenta[i][j] = 1;

                                            } else {
                                                //se adauga arcele in matricea de adiacenta
                                                matriceAdiacenta[i][j] = 1;
                                                matriceAdiacenta[j][i] = 1;
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    pointStart = null;
                    isDragging = false;

                    //se redeseneaza graful
                    repaint();

                   // afisareMatriceAdiacenta(matriceAdiacenta);
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            //evenimentul care se produce la drag&drop pe mousse
            public void mouseDragged(MouseEvent e) {
                pointEnd = e.getPoint();
                isDragging = true;
                if (!modMutareNoduri) {
                    for (int i = 0; i < listaNoduri.size(); i++) {
                        if (NodSurface(listaNoduri.elementAt(i).getCoordX(), listaNoduri.elementAt(i).getCoordY(), pointStart)) {
                            repaint();
                        }
                    }
                } else {
                    if (listaNoduri.elementAt(indexNodMutat) != null) {
                        if(canAddNod(e.getX(),e.getY())) {
                            //actualizam coordonatele nodului cu coord pointEnd
                            listaNoduri.elementAt(indexNodMutat).setCoordX(e.getX());
                            listaNoduri.elementAt(indexNodMutat).setCoordY(e.getY());
                        }
                        repaint();
                    }
                }
            }
        });

    }



    //se executa atunci cand apelam repaint()
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);//apelez metoda paintComponent din clasa de baza

        if (bstrongConnectedComponents) {
            for (Arc a : strongConnnectedComponentslistArcs)
                a.drawArrow(g);

            for(Nod n : strongConnectedComponentNods)
                n.drawNod(g,NodDiam);

        }
        else
        {
            //daca este graf orientat, se trag arce cu sageata in capete
            if (grafOrientat) {
                for (Arc a : listaArce) {
                    a.drawArrow(g);
                }
                //deseneaza arcul curent; cel care e in curs de desenare
                if (pointStart != null && !modMutareNoduri) {
                    g.setColor(Color.black);
                    g.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
                }
            }

            //daca nu este graf orientat, se trag arce fara sageata
            else {
                for (Arc a : listaArce) {
                    a.drawArc(g);
                }
                //deseneaza arcul curent; cel care e in curs de desenare
                if (pointStart != null && !modMutareNoduri) {
                    g.setColor(Color.black);
                    g.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
                }
            }

            //deseneaza lista de noduri pentru graf orientat/neorientat
            for (int i = 0; i < listaNoduri.size(); i++) {
                listaNoduri.elementAt(i).drawNod(g, NodDiam);
            }
        }
    }
}
