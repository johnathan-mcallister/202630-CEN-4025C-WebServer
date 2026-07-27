# JMeter web UI load test

This test drives the Todo List application through its HTTP web UI. Each virtual
user repeatedly registers a unique account, logs in, creates a task, completes
it, deletes it, and logs out. A cookie manager gives each virtual user an
independent browser session, and response assertions turn broken UI workflows
into failed samples.

## Prerequisites

- The application and its MySQL database are running.
- Apache JMeter 5.6.3 or later is installed and `jmeter` is on `PATH`.
- The deployed application is reachable at the configured URL.

Open `todo-load-test.jmx` in the JMeter GUI only to inspect or debug it. Run load
tests from the command line so the GUI does not distort measurements.

## Run

Run the Maven lifecycle to execute the test, check its error threshold, and
generate an HTML dashboard:

```powershell
.\mvnw.cmd verify
```

Open `target/jmeter/reports/todo-load-test/index.html` after the run. The
dashboard includes response-time percentiles and distributions, throughput,
active users, latency, HTTP response codes, and error graphs. Graphs use
one-second time buckets so short smoke runs still show useful trends.

From the repository root, the default target is
`http://localhost:8080/webserver_war_exploded` with 10 users ramping up over 10
seconds and running for 60 seconds:

```powershell
jmeter -n -t jmeter/todo-load-test.jmx `
  -l jmeter/results.jtl `
  -e -o jmeter/report
```

Override any target or load property with `-J`:

```powershell
jmeter -n -t jmeter/todo-load-test.jmx `
  -Jprotocol=http `
  -Jhost=localhost `
  -Jport=8080 `
  -JcontextPath=/webserver_war_exploded `
  -Jthreads=50 `
  -JrampUp=30 `
  -Jduration=300 `
  -l jmeter/results.jtl `
  -e -o jmeter/report
```

JMeter requires the results file and report directory not to exist before a
run. Remove or rename old generated output first. Then open
`jmeter/report/index.html` and review:

- error percentage (the goal should be 0%);
- response-time percentiles, especially p90, p95, and p99;
- throughput over time;
- active threads and response times for saturation;
- database/server CPU, memory, and connection counts alongside the report.

Start with the defaults as a smoke load, then increase `threads` and `duration`
gradually. Run against a non-production database: the workflow removes tasks,
but intentionally leaves its generated `loadtest-...@example.test` accounts in
the database.
