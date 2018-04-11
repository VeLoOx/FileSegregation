class Main {
    public static void main(String[] args) {
        DirectoriesCreator creator = new DirectoriesCreator();
        creator.createDirectoriesStructure();
        FilesSegregation segregator = new FilesSegregation();
    }
}
