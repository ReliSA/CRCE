package cz.zcu.kiv.crce.repository;

public interface PausableStore extends Store {
	
	void startResolve();
	void pauseResolve();
	void resumeResolve();

}
