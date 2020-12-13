package ru.smak.gui.graphics.video.mediaprocessor.managers.videomanager;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;
import ru.smak.gui.graphics.fractalcolors.Colorizer;
import ru.smak.gui.graphics.video.mediaprocessor.managers.Manager;
import ru.smak.math.Fractal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Класс реализующий создание видеофайла из фрагментов фрактала
 */
public class VideoManager extends Manager {
    // Список, в котором хранятся потоки-производителей
    private final ArrayList<Thread> threads = new ArrayList<>();
    // Список списков, в которых будут храниться кадры
    private ArrayList<ArrayList<BufferedImage>> queues = new ArrayList<>();
    // Имя файла, который будет создан в указанном пути
    private String outputFileName = "D:\\myVideo.mp4";

    public void setOutputName(String name) {
        if (!name.isEmpty() && name.endsWith(".mp4"))
            outputFileName = name;
    }

    // Список производителей кадров
    private final ArrayList<FramesCreator> creators = new ArrayList<>();

    private int videoTime = 10;
    private double frameRate = 25;
    // Поток, который будет выполнять роль потребителя
    private Thread consumer = null;

    /**
     * Метод для публичного доступа к запуску создания видео
     *
     * @param planes области фрактала, на основе которых будет создано видео
     */
    public void createVideo(ArrayList<CartesianScreenPlane> planes) {
        packAndEncode(planes);
    }

    /**
     * Метод, внутри которого происходит весь процесс создания видео
     *
     * @param planes области фрактала, на основе которых будет создано видео
     */
    private void packAndEncode(ArrayList<CartesianScreenPlane> planes) {
        /*
        Фрагменты фрактала, на основе которых будет создаваться видео
        делятся на специальные пары
        * */
        // Количество пар
        var pairsCount = 0;
        // Число фреймов для одной пары
        var frameCount = videoTime * frameRate;
        // Число доступных логических процессоров, на которых будут работать производители и потребитель
        var tdsCount = Runtime.getRuntime().availableProcessors();
        // Список специальных пар
        ArrayList<PlanePair> pairs = new ArrayList<>();
        // Заполнение списка
        for (int i = 1; i < planes.size(); i++) {
            pairs.add(new PlanePair(
                    new CartesianScreenPlane(prefWidth, prefHeight,
                            planes.get(i - 1).xMin, planes.get(i - 1).xMax, planes.get(i - 1).yMin, planes.get(i - 1).yMax),
                    new CartesianScreenPlane(prefWidth, prefHeight,
                            planes.get(i).xMin, planes.get(i).xMax, planes.get(i).yMin, planes.get(i).yMax)
            ));
        }
        // Запоминаем количество пар
        pairsCount = pairs.size();
        /*
        * Выбираем количество создаваемых потоков производителей, каждому из которых
        * будет присвоено свое место в списке списков изображений queues
        * */
        var arraySize = Math.min(pairsCount, tdsCount);
        /*
        * Если количество создаваемых потоков-производителей ровно столько,
        * сколько логических процессоров, то уменьшаем количество создаваемых
        * потоков-производителей на 1, дабы оставить один процессор для потока-потребителя
        * */
        if (arraySize == tdsCount) arraySize--;
        // Инициализируем нашу псевдо-очередь производителей
        queues = new ArrayList<>();
        /*
        * Заполняем список нулями, т.к. каждый производитель сам инициализирует нужный для него список
        * создаваемых изображений
        * */
        for (int i = 0; i < arraySize; i++) {
            queues.add(null);
        }
        // Заполняем наши списки производителей и их потоков
        for (int i = 0; i < arraySize; i++) {
            creators.add(new FramesCreator(fractal, colorizer).setQueues(queues).setFrameCount(frameCount));
            threads.add(new Thread(creators.get(i)));
        }
        var j = 0;
        // Поочередно подаем каждому производителю пару из заполненного ранее списка пар
        for (int i = 0; i < pairsCount; i++) {
            if (j == creators.size())
                j = 0;
            creators.get(j).addPair(pairs.get(i));
            j++;
        }
        // Вызываем метод запуска потока-потребителя
        startConsume();
        for (var t : threads)
            t.start();

    }

    private void startConsume() {
        // Инициализируем поток потребителя
        consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                // Внутри него распологаем наш writer, который будет кодировать кадры в видео
                IMediaWriter writer = ToolFactory.makeWriter(outputFileName);
                // Добавляем видеопоток, в который будут закодированы наши кадры
                writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, prefWidth, prefHeight);
                // Задаём время для одного кадра в последовательности видеопотока
                long nextFrameTime = 0;
                // Шаг времени
                long dt = Global.DEFAULT_TIME_UNIT.convert(25, TimeUnit.MILLISECONDS);
                System.out.println("Consumer thread start his work");
                // Цикл, внутри которого будет происходить процесс потребления
                while (true) {
                    // k - счетчик количества завершивших свою работу производителей
                    var k = 0;
                    for (var c : creators) {
                        if (!c.isAlive()) k++;
                    }
                    /*
                    * Если количество завершивших свою работу производителей равно количеству производителей,
                    * то цикл потребления завершается
                    * */
                    if (k == creators.size()) break;
                    // k - счетчик количества производителей, завершивших работу над одной парой
                    k = 0;
                    for (var c : creators) if (c.isReady()) k++;
                    /*
                    * Если количество производителей, завершивших работу над одной парой столько,
                    * сколько самих производителей, то начинается процесс потребления результатов
                    * */
                    if (k == creators.size()) {
                        var j = 0;
                        for (int i = 0; i < creators.size(); i++) {
                            if (j == i && creators.get(j).isReady()) {
                                System.out.println("Getting consume index: " + j);
                                // Цикл, внутри которого происходит кодирование кадров в видеопоток, который мы создали ранее
                                for (int r = 0; r < queues.get(i).size(); r++) {
                                    // Получаем готовый кадр
                                    var img = queues.get(i).get(r);
                                    // Конвертируем в тот тип, который воспринимает энкодер
                                    var correctImg = convertToType(img, BufferedImage.TYPE_3BYTE_BGR);
                                    // Кодируем кадр в видеопоток с определенным временем
                                    writer.encodeVideo(0, correctImg, nextFrameTime, Global.DEFAULT_TIME_UNIT);
                                    // Делаем шаг времени
                                    nextFrameTime += dt;
                                }
                                synchronized (queues.get(i)) {
                                    // вычищаем уже ненужные кадры
                                    queues.get(i).clear();
                                    // снимаем поток производителя с режима ожидания
                                    queues.get(i).notify();
                                    System.out.println("Consuming Index: " + j + " is stopped");
                                    j++;
                                }
                            }
                        }
                    }
                }
                writer.close();
                System.out.println("Encoding is done!");
            }
        });
        consumer.start();
    }

    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        } else {
            image = new BufferedImage(sourceImage.getWidth(),
                    sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }

    @Override
    public void loadFractalData(Fractal fractal, Colorizer colorizer) {
        this.fractal = fractal;
        this.colorizer = colorizer;
    }

    @Override
    public void setPrefScreen(Dimension pref) {
        prefWidth = pref.width;
        prefHeight = pref.height;
    }
}
