package ru.smak.gui.graphics.video.mediaprocessor.managers.videomanager;

import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;
import ru.smak.gui.graphics.fractalcolors.Colorizer;
import ru.smak.gui.graphics.painters.FractalPainter;
import ru.smak.math.Fractal;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Класс производителя кадров
 * */
public class FramesCreator implements Runnable{
    // Список псевдо-очереди, который будет ссылочно привязан к queues из класса VideoManager
    private ArrayList<ArrayList<BufferedImage>> queues = null;
    // Фрактал для создаваемого FractalPainter
    private Fractal fractal;
    // Цветовая схема для создаваемого FractalPainter
    private Colorizer colorizer;
    // Количество создаваемых кадров
    private double frameCount = 376;
    public FramesCreator setFrameCount(double count){
        frameCount = count;
        return this;
    }
    // Показатель активности производителя
    private boolean isAlive = false;
    public boolean isAlive(){
        return isAlive;
    }
    // Показатель готовности кадров производителя
    private boolean isReady = false;
    public boolean isReady(){
        return isReady;
    }
    // Внутренний индекс производителя
    private int ownIndex;
    // Внутренний список пар, на основе которых будут созданы кадры
    private final ArrayList<PlanePair> pairs = new ArrayList<>();
    /**
     * Метод добавления пары во внутренний список пар производителя
     *
     * @param pair добавляемая пара
     * */
    public void addPair(PlanePair pair){
        pairs.add(pair);
    }
    /**
     * Метод удаления пары из внутреннего списка производителя
     *
     * @param pair удаляемая пара
     * */
    public void removePair(PlanePair pair){
        pairs.remove(pair);
    }
    /**
     * Метод удаления пары из внутренного списка производителя
     *
     * @param index индекс удаляемой пары
     * */
    public void removePair(int index){
        pairs.remove(index);
    }
    /**
     * Конструктор
     *
     * @param fractal фрактал для внутреннего FractalPainter
     * @param colorizer цветовая схема для внутреннего FractalPainter
     * */
    public FramesCreator(Fractal fractal, Colorizer colorizer){
        this.fractal = fractal;
        this.colorizer = colorizer;
    }
    /**
     * Метод для занимания места в общем списка производителей
     *
     * @param queues список, в котором производитель займет свое место
     *
     * @return производитель
     * */
    public FramesCreator setQueues(ArrayList<ArrayList<BufferedImage>> queues){
        // ссылочная привязка
        this.queues = queues;
        for(int i = 0; i < queues.size(); i++){
            if(queues.get(i) == null){
                ownIndex = i;
                queues.set(i, new ArrayList<BufferedImage>());
                break;
            }
        }
        return this;
    }
    /**
     * Метод для заполнения данных одного объекта CartesianScreenPlane
     * данными другого объекта CartesianScreenPlane
     *
     * @param filling заполняемый объект
     * @param filler заполняющий объект
     * */
    public void fillPlane(CartesianScreenPlane filling, CartesianScreenPlane filler){
        filling.setWidth(filler.getWidth());
        filling.setHeight(filler.getHeight());
        filling.xMin = filler.xMin;
        filling.xMax = filler.xMax;
        filling.yMin = filler.yMin;
        filling.yMax = filler.yMax;
    }

    @Override
    public void run() {
        if(queues != null){
            // Если поток запущен, то активность производителя true
            isAlive = true;
            // Плоскость, которая будет основой для FractalPainter
            var plane = new CartesianScreenPlane(0, 0, 0, 0,0 ,0);
            // Объект FractalPainter, который будет рисовать новые кадры
            var painter = new FractalPainter(plane, fractal);
            painter.col = colorizer;
            // Процесс рисования кадров
            for(int i = 0; i < pairs.size(); i++){
                // Изначально ставим готовность кадров false
                isReady = false;
                System.out.println("Index: " + ownIndex + " - PairIndex: " + i);
                // Переобозначиваем выбранную пару
                var p = pairs.get(i);
                // Заполняем наш plane данным выбранной пары
                fillPlane(plane, p.getFirst());
                // Запоминаем картинку, нарисованную рисовальщиком
                var img = painter.getSavedImage();
                synchronized (queues){
                    // Доабвляем нулевой кадр в список готовых кадров производителя
                    queues.get(ownIndex).add(img);
                }
                // Вычисляем приращения координат
                var dXMin = (p.getSecond().xMin - p.getFirst().xMin)/frameCount;
                var dXMax = (p.getSecond().xMax - p.getFirst().xMax)/frameCount;
                var dYMin = (p.getSecond().yMin - p.getFirst().yMin)/frameCount;
                var dYMax = (p.getSecond().yMax - p.getFirst().yMax)/frameCount;
                for(int j = 0; j < frameCount; j++){
                    // Изменяем координаты нашего plane
                    plane.xMin += dXMin;
                    plane.xMax += dXMax;
                    plane.yMin += dYMin;
                    plane.yMax += dYMax;
                    // Запоминаем полученную картинку
                    img = painter.getSavedImage();
                    synchronized (queues){
                        // Добавляем картинку в список готовых кадров производителя
                        queues.get(ownIndex).add(img);
                    }
                }
                synchronized (queues.get(ownIndex)){
                    try{
                        System.out.println("Index: " + ownIndex + " is getting wait");
                        // Готовность ставим на true, т.к. все кадры производителя для выбранной пары готовы
                        isReady = true;
                        /*
                        * Ставим производителя в режим ожидания, продлится этот режим
                        * до тех пор, пока потребитель не снимет данного производителя с режима ожидания
                        * */
                        queues.get(ownIndex).wait();
                        System.out.println("Index: " + ownIndex + " is getting continue work");
                    }catch (InterruptedException exception){}
                }
            }
            // Обработка всех пар завершена, активность производителя ставим на false
            isAlive = false;
            System.out.println(ownIndex + " - I'm done!");
        }
    }
}
