import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.security.auth.login.AccountLockedException;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class MergeSort {
    enum ConsoleColors{
        RESET("\033[0m"), RED("\033[0;31m");
        public final String color;

        ConsoleColors(String s) {
            color = s;
        }
    }
    enum ModeSort{
        ABS, DEC
    }
    enum TypeData{
        SYMBOL, INTEGER
    }
    public static final int MAX_COUNT_RECORD = 1000;

    private final TypeData typeData;
    private final ModeSort modeSort;

    public MergeSort(TypeData inTypeData, ModeSort inModeSort){
        this.typeData = inTypeData;
        this.modeSort = inModeSort;
    }

    /**
     * Метод проверяет строку на корректность
     * @param line строка, котоую необходимо проверить
     * @return если в строке нет пробельного символа, тогда возращается значение true, иначе false
     */
    boolean isCorrectLine(String line){
        return line != null && line.indexOf(' ') == -1;
    }
    /**
     * Метод проверяет строку на числовое значение
     * @param line строка, котоую необходимо проверить
     * @return если строка состоит из чисел, тогда возращается значение true, иначе false
     */
    boolean isNumeric(String line){
        try {
            Integer.parseInt(line);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    /**
     * Метод загружает данные из файла в буфер
     * @param pathToFile путь до файла, который нужно загрузить в буфер для считывания
     * @return данный метод возвращает обьект BufferedReader
     */
    private BufferedReader loadFile(String pathToFile){
        try {
            if(pathToFile != null) {
                FileReader reader = new FileReader(pathToFile);
                return new BufferedReader(reader);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Метод удаления файлов
     * @param pathToFile путь до файла, который нужно удалиь
     */
    private void delete_file(String pathToFile){
        try{
            File file = new File(pathToFile);
            if(file.exists()) {
                var res = file.delete();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Метод удаляет файлы слияния, кроме файла вывода и текущего файла слияния
     * @param pathToFile путь до текущего файла слияния
     */
    private void delete_file_merge(String pathToFile){
        try{
            File file = new File(pathToFile);
            for (File f : Objects.requireNonNull(file.getParentFile().listFiles())) {
                if ((f.getName().endsWith("left.txt") || f.getName().endsWith("right.txt")) &&
                        !f.getName().equals(file.getName())) {
                    delete_file(f.getPath());
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Метод возращает строку из буфера
     * @param buff буфер, который хранит буферизованные данные из потока вывода заданного файла
     * @return данный метод возвращает проверенную строку, исходя из заданного параметра typeData
     */
    String getLineFromFile(BufferedReader buff){
        try {
            for(String line; (line = buff.readLine()) != null;){
                if(typeData.equals(TypeData.INTEGER) && isNumeric(line)){
                    return line;
                }else if(typeData.equals(TypeData.SYMBOL) && isCorrectLine(line)){
                    return line;
                }
            }
            return buff.readLine();
        }catch (Exception e){
            return null;
        }
    }
    /**
     * Метод загружает данные в файл
     * @param sorted_data данные, которые прошли сортировку слияния
     * @param pathToFile путь до файла, в который требуется загрузить данные
     */
    void recordResult(List<String> sorted_data, String pathToFile){
        try{
            File file = new File(pathToFile);
            if(!file.exists()){
                if(!file.createNewFile()){
                    System.out.println(ConsoleColors.RED.color+"Error: file cannot be created!"+
                            ConsoleColors.RESET.color);
                    return;
                }
            }
            BufferedWriter buffer = new BufferedWriter(new FileWriter(file, true));
            for(String value : sorted_data){
                buffer.write(value.concat("\n"));
            }
            buffer.flush();
            buffer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Метод сравнивания чисел представленных в виде строк
     * @param val_left_str строчное значение левой переменной
     * @param val_right_str строчное значение правой переменной
     * @return данный метод возвращает целочисленнное значение
     */
    int compareNumericTo(String val_left_str, String val_right_str){
        int val_left = Integer.parseInt(val_left_str), val_right = Integer.parseInt(val_right_str);
        if(val_left > val_right){
            return -1;
        }else if(val_left < val_right){
            return 1;
        }
        return 0;
    }
    /**
     * Метод проверки правильности сортировки последовательности считанной из файла
     * @param pre_value предыдущая считанная строка
     * @param line новая строка
     * @return данный метод возвращает boolean значение в зависимости от условий проверки
     */
    boolean checkValueSorted(String pre_value, String line){
        if(modeSort.equals(ModeSort.ABS)){
            if(typeData.equals(TypeData.INTEGER)){
                return compareNumericTo(line, pre_value) <= 0;
            }else{
                return pre_value.compareTo(line) <= 0;
            }
        }else if(modeSort.equals(ModeSort.DEC)){
            if(typeData.equals(TypeData.INTEGER)){
                return compareNumericTo(line, pre_value) >= 0;
            }else {
                return pre_value.compareTo(line) >= 0;
            }
        }
        return false;
    }
    /**
     * Метод добавляет данные в отсортированный массив
     * @param sorted_array отсортированный массив
     * @param line строка, которую требуется добавить
     * @param pre_value предыдущее считанное значение из файла
     * @param buff буфер, который хранит буферизованные данные из потока вывода заданного файла
     * @return данный метод возвращает новую строку из файла
     */
    String addLineInSortArray(List<String> sorted_array, String line,
                              StringBuilder pre_value, BufferedReader buff){
        if(sorted_array != null && line != null && buff != null) {
            if(pre_value != null && checkValueSorted(pre_value.toString(), line)){
                sorted_array.add(line);
                pre_value.delete(0, pre_value.length());
                pre_value.append(line);
            }
            return getLineFromFile(buff);
        }
        return "";
    }
    /**
     * Метод проверяет перенаполненность массива и загружает данные в файл
     * @param data данные, которые требуется добавить в файл
     * @param pathToFile путь до файла, в который нужно добавить данные
     * @param force_entry поле позволяющее принудительно добавить данные
     */
    void loadDataInFiles(List<String> data, String pathToFile, boolean force_entry){
        if((force_entry && data.size() > 0) || data.size() > MAX_COUNT_RECORD){
            recordResult(data, pathToFile);
            data.clear();
        }
    }
    /**
     * Метод обрабатывает и закрывает буфер
     */
    void closeBuffer(BufferedReader buff){
        try{
            if(buff != null) {
                buff.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Метод сравниевает строки в зависимости от заданных параметров
     * @return данный метод возвращает boolean значение
     */
    boolean compareLinesTo(String left, String right){
        if(typeData.equals(TypeData.INTEGER)) {
            int numberLeftLine = Integer.parseInt(left),
                    numberRightLine = Integer.parseInt(right);
            return (modeSort.equals(ModeSort.ABS) ?
                    numberLeftLine >= numberRightLine : numberLeftLine <= numberRightLine);
        }else{
            return (modeSort.equals(ModeSort.ABS) ?
                    right.compareTo(left) <= 0 : right.compareTo(left) >= 0);
        }
    }
    /**
     * Метод производит слияние данных из двух файлов
     * @param leftBuff буфер одного из файлов
     * @param rightBuff буфер одного из файлов
     * @param pathToResFile путь до файла, в который требуется записать результат слияние двух файлов
     * @return данный метод возвращает обьект BufferedReader файла слияния
     */
    BufferedReader MergeFile(BufferedReader leftBuff, BufferedReader rightBuff, String pathToResFile){
        String line_left = getLineFromFile(leftBuff),
                line_right = getLineFromFile(rightBuff);
        List<String> sorted_text = new ArrayList<>();

        StringBuilder pre_value_left = new StringBuilder((line_left == null ? " " : line_left)),
                pre_value_right =  new StringBuilder((line_right == null ? " " : line_right));

        while(line_left != null || line_right!= null){
            if(line_left != null && line_right != null) {
                if (compareLinesTo(line_left, line_right)) {
                     line_right = addLineInSortArray(sorted_text, line_right, pre_value_right, rightBuff);
                }else {
                    line_left = addLineInSortArray(sorted_text, line_left, pre_value_left, leftBuff);
                }
            }else {
                if (line_left != null) {
                    line_left = addLineInSortArray(sorted_text, line_left, pre_value_left, leftBuff);
                } else{
                    line_right = addLineInSortArray(sorted_text, line_right, pre_value_right, rightBuff);
                }
            }
            loadDataInFiles(sorted_text, pathToResFile, false);
        }
        loadDataInFiles(sorted_text, pathToResFile, true);

        closeBuffer(leftBuff);
        closeBuffer(rightBuff);

        delete_file_merge(pathToResFile);

        return loadFile(pathToResFile);
    }
    /**
     * Метод генерирует имя файлов слияния
     * @param pathToFile путь до файла в который требуется записать результат
     * @param name имя новго файла
     * @return данный метод возвращает сгенерированное название фала слияния
     */
    private @NotNull String createNameFileMerge(String pathToFile,  String name){
        if(name != null && pathToFile != null) {
            String nameFile = new File(pathToFile).getName().split("\\.")[0];
            return new File(pathToFile).getParent().concat("\\").concat(nameFile).concat(name).concat(".txt");
        }else if(pathToFile != null){
            return new File(pathToFile).getParent().concat("\\").concat("unknown.txt");
        }else{
            return "unknown.txt";
        }
    }
    /**
     * Метод делит массив по полам
     */
    private void SplitArray(@NotNull List<String> array, @NotNull List<String> left,
                            @NotNull List<String> right) {
        int indSep = array.size() / 2;
        left.addAll(array.subList(0, indSep));
        right.addAll(array.subList(indSep, array.size()));
    }
    /**
     * Метод сортировки слиянием данных представленных в файлах
     * @param files массив путей до файлов в которых хранятся данные для сортировки
     * @param pathToFile путь до результируещего файла
     * @return данный метод возвращает буфер на результирующий файл сортировки слиянием
     */
    public BufferedReader Sort(List<String> files, String pathToFile){
        delete_file(pathToFile);

        List<String> left = new ArrayList<>(), right = new ArrayList<>();
        SplitArray(files, left, right);

        BufferedReader leftBuff, rightBuff;

        if(left.size() > 1){
            leftBuff = Sort(left, createNameFileMerge(pathToFile, "_left"));
        }else {
            leftBuff = loadFile(left.get(0));
        }

        if(right.size() > 1) {
            rightBuff = Sort(right, createNameFileMerge(pathToFile,"_right"));
        }else{
            rightBuff = loadFile(right.get(0));
        }

        return MergeFile(leftBuff, rightBuff, pathToFile);
    }
    /**
     * Метод сравнивает значение с возвожными параметрами
     * @param parameters возможные параметры с которыми необходимо сравнить переданное значение
     * @param value строковое значение
     * @return данный метод возвращает boolean значение
     */
    static boolean compareParametersTo(String[] parameters,  String value){
        if(parameters == null || value == null) {
            return false;
        }

        for(String parameter : parameters){
            if(parameter.compareTo(value) == 0){
                return true;
            }
        }
        return false;
    }

    public static void main(@NotNull String[] args) {
        TypeData typeData = TypeData.SYMBOL;
        ModeSort modeSort = ModeSort.ABS;
        List<String> files;

        int indFileName = 2;

        if(args == null || args.length <= 2){
            System.out.println(ConsoleColors.RED+"Warnings: Too few arguments!" +ConsoleColors.RESET);
            return;
        }

        if(compareParametersTo(new String[]{"-i", "-s" }, args[0])){
            typeData = (args[0].compareTo("-i") == 0 ? TypeData.INTEGER : TypeData.SYMBOL);
            if(compareParametersTo(new String[]{"-a", "-d" }, args[1])){
                modeSort = (args[1].compareTo("-d") == 0 ? ModeSort.DEC : ModeSort.ABS);
            }else{
                indFileName = 1;
            }
        }else if(compareParametersTo(new String[]{"-i", "-s" }, args[1])){
            modeSort = (args[0].compareTo("-d") == 0 ? ModeSort.DEC : ModeSort.ABS);
            typeData = (args[1].compareTo("-i") == 0 ? TypeData.INTEGER : TypeData.SYMBOL);
        }else{
            indFileName = 1;
        }

        String pathToResFile = args[indFileName++];
        files = Arrays.stream(Arrays.copyOfRange(args, indFileName, args.length)).collect(Collectors.toList());
        MergeSort mergeSort = new MergeSort(typeData, modeSort);
        var buff = mergeSort.Sort(files, pathToResFile);
        mergeSort.closeBuffer(buff);
    }
}
