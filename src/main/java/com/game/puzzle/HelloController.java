package com.game.puzzle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class HelloController {

    @FXML
    private Pane Place_for_Solve;

    @FXML
    private Pane Place_for_divide_puzzle;

    @FXML
    private Label welcomeText;

    @FXML
    private ImageView main_photo;

    Image image;

    {
        try {
            image = new Image(new FileInputStream("D:\\Work\\Стажування\\DevCom Java\\Try_4_JavaFX\\puzzle\\src\\main\\resources\\com\\game\\puzzle\\picture_2.jpg"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private double initialX;  // for puzzle_moving
    private double initialY;  // for puzzle_moving

    private List<Example_Puzzle> list = new ArrayList<>();
    private List<Example_Puzzle> auto_solved_list = new ArrayList<>();



    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
       // Example_Puzzle examplePuzzle = Find_first_puzzle();
        solve_puzzle();
        //Auto_solve_algorithm();
        //Find_first_puzzle();
        //dly_sebe();
    }


    @FXML
    void initialize() throws FileNotFoundException {


        main_photo.setImage(image);

        divide(image);

        //dly_sebe();

    }



    private void divide (Image image){


        PixelReader pixelReader = image.getPixelReader();

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        System.out.println(image.getWidth() + " : " + image.getHeight());


        int tileSize = 93;
        int tileSizeX = (width / 4) ;
        int tileSizeY = (height / 4) ;
        int numCols = width / tileSizeX;
        int numRows = height / tileSizeY;

        System.out.println(numCols);



        for (int row = 0, y_iv = 0; row < numRows; row++) {
            y_iv += tileSize + row; //тимчасово
            for (int col = 0, x_iv = 0; col < numCols; col++) {
                x_iv += tileSize + col; // тимчасово

                WritableImage tile = new WritableImage(tileSizeX, tileSizeY);

                PixelWriter pixelWriter = tile.getPixelWriter();

                for (int y = 0; y < tileSizeY; y++) {
                    for (int x = 0; x < tileSizeX; x++) {

                        Color color = pixelReader.getColor(col * tileSizeX + x, row * tileSizeY + y);

                        pixelWriter.setColor(x, y, color);
                    }
                }




                Example_Puzzle iv = new Example_Puzzle(tile);
                iv.setTranslateX(x_iv - tileSizeX + 5);
                iv.setTranslateY(y_iv - tileSizeY + 5);

                Example_Puzzle new_iv = (Example_Puzzle) puzzle_moving(iv);
                list.add(new_iv);

                // Додавання куска до контейнера
                Place_for_divide_puzzle.getChildren().add(new_iv);


            }
        }




    }



    private ImageView puzzle_moving(ImageView imageView){


        imageView.setOnMousePressed(event -> {
            initialX = event.getSceneX() - imageView.getTranslateX();
            initialY = event.getSceneY() - imageView.getTranslateY();

            System.out.println(event.getSceneX() + " : " + event.getSceneY());
            System.out.println(imageView.getTranslateX() + " : " + imageView.getTranslateY());

            System.out.println( "LIST: " +  list.get(1).getTranslateX());
        });


        imageView.setOnMouseDragged(event -> {
            double offsetX = event.getSceneX() - initialX;
            double offsetY = event.getSceneY() - initialY;
            imageView.setTranslateX(offsetX);
            imageView.setTranslateY(offsetY);
        });


//        imageView.setOnMouseReleased(event -> {
//
//        });



        return imageView;



    }





    private void solve_puzzle (){
        if (!auto_solved_list.isEmpty()) auto_solved_list = new ArrayList<>();

        Example_Puzzle firstPuzzle = Find_first_puzzle();

        Example_Puzzle newColum = null;

        while (auto_solved_list.size() < list.size()) {
            while (firstPuzzle != null) {

                Example_Puzzle nextPuzzle = get_Puzzle(firstPuzzle.get_Height_Right_Colors(), TypeOfSolve.LEFT);

                if (nextPuzzle != null) Add_puzzle(firstPuzzle, nextPuzzle, TypeOfSolve.RIGHT);

                firstPuzzle = nextPuzzle;

            }

            if (newColum == null) newColum = auto_solved_list.get(0);

            Example_Puzzle nextPuzzle = get_Puzzle(newColum.get_Width_Bottom_Colors(), TypeOfSolve.TOP);
            if (nextPuzzle != null) Add_puzzle(newColum, nextPuzzle, TypeOfSolve.TOP);
            newColum = nextPuzzle;
            firstPuzzle = newColum;



        }

        System.out.println(auto_solved_list.size());


    }

    private Example_Puzzle Find_first_puzzle(){

        Example_Puzzle find_First = list.get(0);
        List<Example_Puzzle> find_list = new ArrayList<>();

        while (find_First != null){
            find_list.add(find_First);
            find_First = get_Puzzle(find_First.get_Height_Left_Colors(), TypeOfSolve.RIGHT);
        }

        find_First = find_list.get(find_list.size() - 1);

        while (find_First != null){
            find_list.add(find_First);
            find_First = get_Puzzle(find_First.get_Width_Top_Colors(), TypeOfSolve.BOTTOM);
        }
        //Place_for_Solve.getChildren().add(find_list.get(find_list.size() - 1));
        return find_list.get(find_list.size() - 1);
    }



    private void Add_puzzle(Example_Puzzle present, Example_Puzzle next, TypeOfSolve type){


        if (!auto_solved_list.contains(present)){
            present.setTranslateX(0);
            present.setTranslateY(0);
            auto_solved_list.add(present);
        }


        if (type == TypeOfSolve.LEFT || type == TypeOfSolve.RIGHT && present.getTranslateX() + present.getImage().getWidth() < image.getWidth() ) {
            System.out.println(present.getTranslateX() + " КАРТИНА ");
            next.setTranslateX(present.getTranslateX() + present.getImage().getWidth() + 2);
            next.setTranslateY(present.getTranslateY());
        }else {
            next.setTranslateX(present.getTranslateX() );
            next.setTranslateY(present.getTranslateY() + present.getImage().getHeight() + 2);
        }
        auto_solved_list.add(next);



    }



    private Example_Puzzle get_Puzzle(List<Color> s1 , TypeOfSolve type){



        //List<Color> s1 = list.get(11).get_Height_Left_Colors();


        List<Good> listGood  = new ArrayList<>();


        for (int li = 0; li < list.size()  ; li++) {

            int ss = 0;

            List<Color> s2 = null;

            if (type == TypeOfSolve.BOTTOM)
                s2 = list.get(li).get_Width_Bottom_Colors();
            else if (type == TypeOfSolve.TOP)
                s2 = list.get(li).get_Width_Top_Colors();
            else if (type == TypeOfSolve.LEFT)
                s2 = list.get(li).get_Height_Left_Colors();
            else if (type == TypeOfSolve.RIGHT)
                s2 = list.get(li).get_Height_Right_Colors();




            System.out.println(s1.size() + " : " + s2.size());


            for (int i = 0; i < s2.size(); i++) {


                double r1 = s1.get(i).getRed() - s2.get(i).getRed();
                double g1 = s1.get(i).getGreen() - s2.get(i).getGreen();
                double b1 = s1.get(i).getBlue() - s2.get(i).getBlue();

                boolean trust = r1 < 0.2 && r1 > -0.2 && g1 < 0.2 && g1 > -0.2 && b1 < 0.2 && b1 > -0.2;

                if (trust) ss++;

                // System.out.println(r1 + " : " + g1 + " : " + b1);

            }

            System.out.println(ss);

            listGood.add(new Good(ss, list.get(li)));



        }



        try {
            Example_Puzzle examplePuzzle;

            if (type == TypeOfSolve.BOTTOM || type == TypeOfSolve.TOP ) {
                examplePuzzle = listGood.stream().max(Comparator.comparing(x -> x.сoef)).
                        filter(x -> x.сoef > x.examplePuzzle.getImage().getWidth() * 0.8).
                        stream().findFirst().get().examplePuzzle;
            }else {
                examplePuzzle = listGood.stream().max(Comparator.comparing(x -> x.сoef)).
                        filter(x -> x.сoef > x.examplePuzzle.getImage().getHeight() * 0.8).
                        stream().findFirst().get().examplePuzzle;
            }

            System.out.println();
            return examplePuzzle;
            //Place_for_Solve.getChildren().addAll(list.get(11), examplePuzzle);

        }catch (NoSuchElementException e){
            System.out.println("Немає підходящого пазла");
        }


        return null;
    }







    class Example_Puzzle extends ImageView {

       private int index_for_solve;
       private int width;
       private int height;

        public int getIndex_for_solve() {
            return index_for_solve;
        }

        public void setIndex_for_solve(int index_for_solve) {
            this.index_for_solve = index_for_solve;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public Example_Puzzle() {
        }

        public Example_Puzzle(String s) {
            super(s);
        }

        public Example_Puzzle(Image image) {
            super(image);
        }

        public Example_Puzzle(int width, int height) {
            this.width = width;
            this.height = height;
        }


        private  PixelReader pixelReader = getImage().getPixelReader();



        public   List<Color> get_Width_Top_Colors(){

            System.out.println("Example_Puzzle.get_Width_Top: (getImage().getWidth()) " + getImage().getWidth());

            List<Color> list_color = new ArrayList<>();

                for (int x = 0; x < getImage().getWidth(); x++) {
                    Color color = pixelReader.getColor(x, 0);
                    list_color.add(color);
                }

            return list_color;
        }



        public   List<Color> get_Width_Bottom_Colors(){

            System.out.println("Example_Puzzle.get_Width_Bottom: (getImage().getWidth()) " + getImage().getWidth());

            List<Color> list_color = new ArrayList<>();

            for (int x = 0; x < getImage().getWidth(); x++) {

                Color color = pixelReader.getColor(x, (int) getImage().getHeight() - 1);
                list_color.add(color);
            }

            return list_color;
        }

        public   List<Color> get_Height_Left_Colors(){

            System.out.println("Example_Puzzle.get_Height_Left: (getImage().getWidth()) " + getImage().getWidth());

            List<Color> list_color = new ArrayList<>();

            for (int y = 0; y < getImage().getHeight(); y++) {

                Color color = pixelReader.getColor(0, y);
                list_color.add(color);
            }

            return list_color;
        }

        public   List<Color> get_Height_Right_Colors(){

            System.out.println("Example_Puzzle.get_Height_Right: (getImage().getWidth()) " + getImage().getWidth());

            List<Color> list_color = new ArrayList<>();

            for (int y = 0; y < getImage().getHeight(); y++) {

                Color color = pixelReader.getColor((int) getImage().getWidth() - 1, y);
                list_color.add(color);
            }

            return list_color;
        }

    }




    class Good {  // Для ArrayList щоб знаходити найвищий коефіцієнт
        int сoef;
        final Example_Puzzle examplePuzzle;

        public Good(int сoef, Example_Puzzle examplePuzzle) {
            this.сoef = сoef;
            this.examplePuzzle = examplePuzzle;
        }
    }


    enum TypeOfSolve{
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }



    void dly_sebe(){


        int ss = 0;



        List<Color> s1 = list.get(2).get_Height_Left_Colors();
        List<Color> s2 = list.get(11).get_Height_Right_Colors();

        System.out.println(s1.size() + " : " + s2.size());


        for (int i = 0; i < s2.size(); i++) {


                double r1 = s1.get(i).getRed() - s2.get(i).getRed();
                double g1 = s1.get(i).getGreen() - s2.get(i).getGreen();
                double b1 = s1.get(i).getBlue() - s2.get(i).getBlue();

                boolean trust = r1 < 0.2 && r1 > -0.2 && g1 < 0.2 && g1 > -0.2 && b1 < 0.2 && b1 > -0.2;

                if (trust) ss++;

           // System.out.println(r1 + " : " + g1 + " : " + b1);

        }

        System.out.println(ss);



    }



}