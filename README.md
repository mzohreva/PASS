## PASS - Programming Assignment Submission System

PASS is an scalable web-based solution for programming assignment submission
and automated testing. It is suitable for grading programming assignments in
computer science classes wherein student solutions are tested against a set of
test cases. It can compile and test C/C++ programs out of the box and it can be
extended through bash scripts to support any other programming language.

### Dependencies

PASS has the following run-time dependencies:

- Java 1.8
- MySQL 5.7
- GlassFish 4.1
- Linux environment
- [SimpleLinuxSandbox](https://github.com/mzohreva/SimpleLinuxSandbox) (optional)
- RabbitMQ 3.6.6 (optional)
- jsvc 1.0.6 (optional)

The sandbox is needed for the C/C++ test script. RabbitMQ and jsvc are only
needed if using distributed task scheduler (see the next section).

### Components

The system has multiple components:

- Database server
- File server
- Web server
- Message broker (optional)
- Workers (optional)

Long-running tasks, such as compiling and evaluating submissions, can be
handled in two different ways:

- Using a local task scheduler: tasks are executed in the web application using
  a fixed size thread pool

  or

- Using a distributed task scheduler and one or more workers: tasks are
  executed by standalone workers using RabbitMQ as the messaging backend

The default is to use workers, you can change that by modifying the following
lines in `pass.web.common.Container`:

```java
    public void initialize(ServletContext context)
    {
        try
        {
            //taskManager = new LocalTaskManager(WebConfig.getInstance().getConcurrentTasks());
            taskManager = new DistributedTaskManager();
            ...
```

To use the local task scheduler, comment/uncomment the appropriate lines shown
above.

The file server stores files related to assignments, student submissions, etc.
If using the distributed task scheduler, all workers as well as the web server
need to have access to the file server, database server and the message broker.
To share files stored on the file server among the web server and workers, you
need a distributed file system like NFS. Workers need to have read/write access
to these files.

### How to Deploy

##### Step 1: Create the Database

Use the sql script `create_tables.sql` to generate the MySQL database. Then
create MySQL users for the web application and workers as necessary.

##### Step 2: File Repository

Copy the folder `file_repository` to your preferred location on your file
server.

In order for the students to be able to sign up in the web site, you need to
prepare a list of enrolled students as a CSV file with the following format:

```
student id, username, last name, first name, email
```

For example:

```
1234567890,jdoe,Doe,John,jdoe@gmail.com
```

This file should be stored as `list.csv` in the file repository.

##### Step 3: Generate Master Key

You need Maven 3.0+ to build PASS. After building for the first time, you need
to run the command line tool located in `pass-commandline` which has the
following usage:

```
$ java -jar pass-commandline/target/pass-commandline-1.0.jar
Usage: pass-commandline-1.0.jar COMMAND [ARGS]

COMMAND:

    key:    generate master key
    enc:    encrypt a password with master key
    admin:  register admin user in database
    db:
            generate database creation script
            args: <output_file>
    cfg:
            generate all configuration files
            args: <template_dir> <output_dir>
    cfg1:
            generate a single configuration file
            args: <template> <output>

```

Use the `key` command to generate a master key. This key will be used for
encrypting credentials such as database password in configuration files.
You need to keep this key in a safe place.

##### Step 4: Generate Configuration Files

Use the command line tool with `cfg` command to generate the configuration
files. You need to specify the location of the templates as well as the
output directory:

```
$ java -jar pass-commandline/target/pass-commandline-1.0.jar cfg config_templates/ config/
```

It will ask you to provide various configuration settings step by step. At some
point it will ask you to enter the master key to encrypt passwords.
The following tables summarize the required settings:

| `basic_settings.xml`                | |
|-------------------------------------|---|
| `site_title`                        | The website title displayed in navbar |
| `server_url`                        | The website url, used for referrer validation |
| `keep_submissions_per_user_project` | How many submissions to keep from each user per assignment |

| `broker_settings.xml` | |
|-----------------------|---|
| `broker_host`         | The hostname or IP address of the RabbitMQ server |
| `broker_user`         | The RabbitMQ account username |
| `broker_password`     | The RabbitMQ account password |

| `database_settings.xml` | |
|-------------------------|---|
| `database_url`          | The database connection string |
| `database_username`     | The database account username |
| `database_password`     | The database account password |

| `filesystem_settings.xml` | |
|---------------------------|---|
| `repository_path`         | The filesystem path for storing submissions and other files |
| `master_key_path`         | Where to find the master key |

| `web_settings.xml`     | |
|------------------------|---|
| `server_email_address` | The email address to use for sending verification codes |
| `email_server`         | The smtp server for `server_email_address` |
| `email_user`           | The username for authenticating to the smtp server |
| `email_password`       | The password for authenticating to the smtp server |
| `concurrent_tasks`     | Number of threads for task execution if using `LocalTaskManager` |

Most of these configuration files have two versions: a `.web` version and a
`.worker` version. For example, you might want to have two different database
accounts with different permissions for the web server and the workers.
`filesystem_settings.xml` also has two versions: `.local` and `.remote`. The local
version is used with the web server and the worker running on the same server,
while the remote version is used for workers running on other machines. The
rationale is that these servers might be configured to access the files
repository differently.

There are other configurations that have default values (not listed here). You
can change those settings either in the templates or in the generated
configuration files.

##### Step 5: Create `admin`

After generating the configuration files, re-build PASS. Then use the
command line tool to create the admin user in database:

```
$ java -jar pass-commandline/target/pass-commandline-1.0.jar admin
```

At this point you can deploy the web application and login with the admin user.

##### Step 6: Run Workers (optional)

If you decided to use distributed task scheduling, you will need to run one or
more workers. Use the provided scripts to run workers as daemons. Make sure to
check the worker's log file after starting it. The worker's current log file
can be found at `/tmp/pass_worker0.log`.

Each worker daemon creates two job consumers by default. You can change this
setting in `env.sh`:

```
ARGS="2 $(hostname)"
```

A reasonable number would be the number of CPU cores exclusively available to
the worker.

### Storing the Master Key

Web applications in general are prone to various attacks that could compromise
the information stored on the server. To protect database password and other
credentials stored in configuration files (encrypted with the master key) in
case of such attacks, you need to protect the master key.

Since the web application reads the master key once during its startup, the
file containing the master key can be removed safely after startup. This can be
achieved for example by providing a readable copy of the master key file only
during the startup phase.
