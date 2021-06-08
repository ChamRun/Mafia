interface Role {
    String actQuestion();
    String getName();

}

abstract class Mafia implements Role {

}

class GodFather extends Mafia{

    private boolean detectedBefore = false;

    public boolean hasBeenDetectedBefore(){
        return detectedBefore;
    }

    public void detect(){
        detectedBefore = true;
    }

    @Override
    public String actQuestion() {
        return "Who do you wanna kill?";
    }

    @Override
    public String getName() {
        return "GodFather";
    }
}

class DoctorLector extends Mafia{

    @Override
    public String actQuestion() {
        return "Which Mafia, Do you wanna save, from sniperShot?";
    }

    @Override
    public String getName() {
        return "Doctor Lector";
    }
}

class SimpleMafia extends Mafia{

    @Override
    public String actQuestion() {
        return "Who do you suggest to be killed by Godfather??";
    }

    @Override
    public String getName() {
        return "Simple Mafia";
    }
}


abstract class Citizen implements Role{
}

class CityDoctor extends Citizen{

    private boolean savedCityDrBefore = false;

    public boolean hasSavedCityDrBefore() {
        return savedCityDrBefore;
    }

    public void saveCityDr(){
        savedCityDrBefore = true;
    }

    @Override
    public String actQuestion() {
        return "Who do you save from GodfatherShot?";
    }

    @Override
    public String getName() {
        return "City Doctor";
    }
}

class Mayor extends Citizen{

    @Override
    public String actQuestion() {
        return null;
    }

    @Override
    public String getName() {
        return "Mayor";
    }
}

class Detective extends Citizen{

    @Override
    public String actQuestion() {
        return "Whose role you want to know?";
    }

    @Override
    public String getName() {
        return "Detective";
    }
}


class Psychic extends Citizen{


    @Override
    public String actQuestion() {
        return "Who do you wanna mute for a day?";
    }

    @Override
    public String getName() {
        return "Psychic";
    }
}


class Bulletproof extends Citizen{

    private boolean shot = false;
    private int nInquiry = 2;

    public boolean isShot() {
        return shot;
    }

    public void shot(){
        shot = true;
    }

    public void inquiry(){
        nInquiry--;
    }

    public boolean canInquiry(){
        if (0 < nInquiry){
            return true;
        }

        return false;
    }

    @Override
    public String actQuestion() {
        return "Do you wanna know how many Mafias and Citizens are in game? [yes, no]";
    }

    @Override
    public String getName() {
        return "Bulletproof";
    }
}

class Sniper extends Citizen{

    boolean hasBullet = true;

    @Override
    public String actQuestion() {
        return "Who do you wanna kill?";
    }

    @Override
    public String getName() {
        return "Sniper";
    }
}

class SimpleCitizen extends Citizen{


    @Override
    public String actQuestion() {
        return null;
    }

    @Override
    public String getName() {
        return "Simple Citizen";
    }
}





