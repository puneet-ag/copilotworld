//package panel;
//
//import com.intellij.notification.Notification;
//import com.intellij.notification.NotificationAction;
//import com.intellij.openapi.actionSystem.ActionManager;
//import com.intellij.openapi.actionSystem.ActionUpdateThread;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.components.ServiceManager;
//import com.intellij.openapi.project.DumbAwareAction;
//import panel.converted.CodeGPTBundle;
//import panel.converted.GeneralSettings;
//import panel.converted.OverlayUtil;
//
//
//import java.util.function.Consumer;
//
//import static com.intellij.notification.NotificationAction.createSimpleExpiring;
//
//public abstract class LlamaServerToggleActions extends DumbAwareAction {
//
//    private static final String STARTING = "settingsConfigurable.service.llama.progress.startingServer";
//    private static final String RUNNING = "settingsConfigurable.service.llama.progress.serverRunning";
//    private static final String STOPPING = "settingsConfigurable.service.llama.progress.stoppingServer";
//    private static final String STOPPED = "settingsConfigurable.service.llama.progress.serverStopped";
//    private static final String START = "settingsConfigurable.service.llama.stopServer.opposite";
//    private static final String STOP = "settingsConfigurable.service.llama.startServer.opposite";
//
//    private boolean startServer;
//    private Notification notification;
//
//    public LlamaServerToggleActions(boolean startServer) {
//        this.startServer = startServer;
//    }
//
//    public static void expireOtherNotification(boolean start) {
//        getAction(start).notification.expire();
//        getAction(start).notification = null;
//    }
//
//    private static LlamaServerToggleActions getAction(boolean start) {
//        return (LlamaServerToggleActions) ActionManager.getInstance().getAction(getId(start));
//    }
//
//    private static String getId(boolean start) {
//        return start ? "statusbar.stopServer" : "statusbar.startServer";
//    }
//
//    @Override
//    public void actionPerformed(AnActionEvent e) {
//        if (GeneralSettings.getCurrentState().getSelectedService() != LlamaSettings.getInstance().state) {
//            return;
//        }
//        if (notification != null) {
//            notification.expire();
//        }
//        expireOtherNotification(startServer);
//        LlamaServerAgent llamaServerAgent = ServiceManager.getService(LlamaServerAgent.class);
//        String serverName = LlamaModel.findByHuggingFaceModel(LlamaSettings.getInstance().state.getHuggingFaceModel()).toString();
//        if (startServer) {
//            start(serverName, llamaServerAgent);
//        } else {
//            stop(serverName, llamaServerAgent);
//        }
//    }
//
//    private void start(String serverName, LlamaServerAgent llamaServerAgent) {
//        notification = OverlayUtil.stickyNotification(formatMsg(STARTING, serverName),
//                createSimpleExpiring(CodeGPTBundle.get(STOP), () -> stop(serverName, llamaServerAgent)));
//        ServerProgressPanel serverProgressPanel = new ServerProgressPanel();
//        llamaServerAgent.setActiveServerProgressPanel(serverProgressPanel);
//        LlamaSettings.State settings = LlamaSettings.getInstance().state;
//        llamaServerAgent.startAgent(new LlamaServerStartupParams(
//                        LlamaSettings.getInstance().getActualModelPath(),
//                        settings.getContextSize(),
//                        settings.getThreads(),
//                        settings.getServerPort(),
//                        getAdditionalParametersList(settings.getAdditionalParameters()),
//                        getAdditionalParametersList(settings.getAdditionalBuildParameters()),
//                        getAdditionalEnvironmentVariablesMap(settings.getAdditionalEnvironmentVariables())),
//                serverProgressPanel,
//                () -> {
//                    if (notification != null) {
//                        notification.expire();
//                    }
//                    notification = notification(RUNNING, false, serverName, llamaServerAgent);
//                },
//                (Consumer<ServerProgressPanel>) panel -> {
//                    if (notification != null) {
//                        notification.expire();
//                    }
//                    notification = notification(STOPPED, true, serverName, llamaServerAgent);
//                });
//    }
//
//    private void stop(String serverName, LlamaServerAgent llamaServerAgent) {
//        notification = OverlayUtil.showNotification(formatMsg(STOPPING, serverName));
//        llamaServerAgent.stopAgent();
//        if (notification != null) {
//            notification.expire();
//        }
//        notification = notification(STOPPED, true, serverName, llamaServerAgent);
//    }
//
//    private Notification notification(String id, boolean nextStart, String serverName, LlamaServerAgent llamaServerAgent) {
//        return OverlayUtil.showNotification(formatMsg(id, serverName),
//                createSimpleExpiring(CodeGPTBundle.get(nextStart ? START : STOP),
//                        () -> {
//                            if (nextStart) {
//                                start(serverName, llamaServerAgent);
//                            } else {
//                                stop(serverName, llamaServerAgent);
//                            }
//                        }));
//    }
//
//    private String formatMsg(String id, String serverName) {
//        String msg = CodeGPTBundle.get(id);
//        boolean points = msg.endsWith("...");
//        return (points ? msg.substring(0, msg.length() - 3) : msg) + ": " + serverName + (points ? " ..." : "");
//    }
//
//    @Override
//    public void update(AnActionEvent e) {
//        LlamaSettings.State settings = LlamaSettings.getInstance().state;
//        boolean llamaRunnable = isRunnable(settings.getHuggingFaceModel());
//        boolean serverRunning = llamaRunnable && ServiceManager.getService(LlamaServerAgent.class).isServerRunning();
//        boolean toggle = llamaRunnable && (serverRunning != startServer);
//        e.getPresentation().setVisible(toggle);
//        e.getPresentation().setEnabled(toggle);
//    }
//
//    @Override
//    public ActionUpdateThread getActionUpdateThread() {
//        return ActionUpdateThread.BGT;
//    }
//}
//
//
//
//
//
