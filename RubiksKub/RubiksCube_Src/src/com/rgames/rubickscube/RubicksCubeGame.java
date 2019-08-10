package com.rgames.rubickscube;

import com.rgames.rubickscube.controller.GameController;
import com.rgames.rubickscube.model.GameModel;
import com.rgames.rubickscube.view.GameView;

/**
 */
public final class RubicksCubeGame {

    /**
     * The one and only instance of this application.
     */
    private static RubicksCubeGame instance = new RubicksCubeGame(); 
    
    /**
     */
    private GameModel m_model;
    
    /**
     */
    private GameView m_view;

    /**
     */
    private GameController m_controller;
    
    private RubicksCubeGame() {
    }
    
    public static RubicksCubeGame getInstance() {
        return instance;
    }
    
    public void run() {
        m_model = new GameModel();
        m_view = new GameView(m_model);
        m_controller = new GameController(m_model, m_view);
        
        m_view.run();
    }
    
    /**
     * @return the model
     */
    public GameModel getModel() {
        return m_model;
    }
    
    /**
     * @return the view
     */
    public GameView getView() {
        return m_view;
    }

    /**
     * @return the controller
     */
    public GameController getController() {
        return m_controller;
    }
    
    /**
     * @return the version
     */
    public static String getVersion() {
        return "v1.0.0";
    }
}
