Component Repository with Compatibility Evaluation

1) Before start, install needed artifacts to local repository by running dep-install.cmd

2) Application temporary use U: drive as a bundle repository until configuration management will be implemented,
   so substitute some folder to U: drive by following command:
     C:\> subst U: <some-folder>
   or rewrite the line in cz.zcu.kiv.crce.repository.internal.Activator

3) Start the application by running start.bat

4) CRCE runs on following URL:
http://localhost:8090/crce

5) Apache Felix Web Console is accessible on this URL (login: admin, password: admin):
http://localhost:8090/system/console
