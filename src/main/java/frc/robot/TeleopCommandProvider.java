package frc.robot;

import org.hotutilites.hotcontroller.HotController;

import frc.robot.BallSupervisor.BallSupervisorState;
import frc.robot.BallSupervisor.hoodPos;
import frc.robot.BallSupervisor.intakePosition;


public class TeleopCommandProvider extends RobotCommandProvider {

    private HotController driver;
    private HotController operator;
    private RobotState robotState;
    public TeleopCommandProvider(HotController driver, HotController operator,RobotState robotState) {
        this.setDriver(driver);
        this.setOperator(operator);
        this.robotState = robotState;
    }

    public HotController getOperator() {
        return operator;
    }

    public void setOperator(HotController operator) {
        this.operator = operator;
    }

    public double getDriveCommand() {
        return driver.getStickLY();
    }

    public double getTurnCommand() {
        return driver.getStickRX();
    }

    private void setDriver(HotController driver) {
        this.driver = driver;
    }

    @Override
    public void chooseBallCommand() {
        if(driver.getRightTrigger() > 0.5){
            setBallSupervisorState(BallSupervisorState.shoot);
        }else if (operator.getButtonX()){ //config for autoshot
            setBallSupervisorState(BallSupervisorState.prime);
            robotState.setShooterTargetRPM(3800);
            setHoodPosition(hoodPos.autoshot);
        }else if(operator.getButtonY()){//config for trench shot
            setBallSupervisorState(BallSupervisorState.prime);
            robotState.setShooterTargetRPM(4800);
            setHoodPosition(hoodPos.trench);
        }else if(operator.getButtonA()){
            setBallSupervisorState(BallSupervisorState.reject);
        }else if(operator.getButtonLeftBumper()){
            setBallSupervisorState(BallSupervisorState.intakeIn); 
        }else if(operator.getButtonRightBumper()){
            setBallSupervisorState(BallSupervisorState.intakeOut); 
        }else if(operator.getButtonStart()){
            setBallSupervisorState(BallSupervisorState.reset);
        }else{
            setBallSupervisorState(BallSupervisorState.intakeStop); 
            setHoodPosition(hoodPos.goingUnder);
            robotState.setShooterTargetRPM(0);
        }
    }

        
}


