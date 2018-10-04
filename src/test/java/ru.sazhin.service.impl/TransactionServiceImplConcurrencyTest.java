package ru.sazhin.service.impl;

import com.j256.ormlite.dao.Dao;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.sazhin.Database;
import ru.sazhin.model.Account;
import ru.sazhin.model.User;
import ru.sazhin.service.TransactionService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class TransactionServiceImplConcurrencyTest {

    private TransactionService transactionService = new TransactionServiceImpl();
    private Dao<Account, Long> accountDao = Database.getInstance().getDao(Account.class);

    @Before
    public void up() {
        Database.getInstance().createTables();
    }

    @After
    public void down() {
        Database.getInstance().closeConnection();
    }

    @Test
    public void test10Accounts() throws InterruptedException, SQLException {
        User user = new User();
        Account acc0 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc0);
        Account acc1 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc1);
        Account acc2 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc2);
        Account acc3 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc3);
        Account acc4 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc4);
        Account acc5 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc5);
        Account acc6 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc6);
        Account acc7 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc7);
        Account acc8 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc8);
        Account acc9 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc9);

        List<? extends Runnable> transactions = Arrays.asList(
                () -> transactionService.transact(acc0.getId(), acc1.getId(), BigDecimal.valueOf(10)),
                () -> transactionService.transact(acc2.getId(), acc3.getId(), BigDecimal.valueOf(15)),
                () -> transactionService.transact(acc2.getId(), acc0.getId(), BigDecimal.valueOf(10)),
                () -> transactionService.transact(acc0.getId(), acc1.getId(), BigDecimal.valueOf(20)),
                () -> transactionService.transact(acc7.getId(), acc9.getId(), BigDecimal.valueOf(10)),
                () -> transactionService.transact(acc3.getId(), acc7.getId(), BigDecimal.valueOf(10)),
                () -> transactionService.transact(acc0.getId(), acc1.getId(), BigDecimal.valueOf(25)),
                () -> transactionService.transact(acc4.getId(), acc1.getId(), BigDecimal.valueOf(10)),
                () -> transactionService.transact(acc0.getId(), acc1.getId(), BigDecimal.valueOf(15)),
                () -> transactionService.transact(acc8.getId(), acc3.getId(), BigDecimal.valueOf(30)),
                () -> transactionService.transact(acc5.getId(), acc2.getId(), BigDecimal.valueOf(35)),
                () -> transactionService.transact(acc6.getId(), acc9.getId(), BigDecimal.valueOf(25)),
                () -> transactionService.transact(acc3.getId(), acc2.getId(), BigDecimal.valueOf(10)),
                () -> transactionService.transact(acc5.getId(), acc8.getId(), BigDecimal.valueOf(15)),
                () -> transactionService.transact(acc0.getId(), acc6.getId(), BigDecimal.valueOf(10)),
                () -> transactionService.transact(acc9.getId(), acc1.getId(), BigDecimal.valueOf(15))
        );

        assertConcurrent(transactions, 10);

        Assert.assertEquals(BigDecimal.valueOf(930),  accountDao.queryForId(acc0.getId()).getAmount());
        Assert.assertEquals(BigDecimal.valueOf(1095), accountDao.queryForId(acc1.getId()).getAmount());
        Assert.assertEquals(BigDecimal.valueOf(1020), accountDao.queryForId(acc2.getId()).getAmount());
        Assert.assertEquals(BigDecimal.valueOf(1025), accountDao.queryForId(acc3.getId()).getAmount());
        Assert.assertEquals(BigDecimal.valueOf(990),  accountDao.queryForId(acc4.getId()).getAmount());
        Assert.assertEquals(BigDecimal.valueOf(950),  accountDao.queryForId(acc5.getId()).getAmount());
        Assert.assertEquals(BigDecimal.valueOf(985),  accountDao.queryForId(acc6.getId()).getAmount());
        Assert.assertEquals(BigDecimal.valueOf(1000), accountDao.queryForId(acc7.getId()).getAmount());
        Assert.assertEquals(BigDecimal.valueOf(985),  accountDao.queryForId(acc8.getId()).getAmount());
        Assert.assertEquals(BigDecimal.valueOf(1020), accountDao.queryForId(acc9.getId()).getAmount());
    }

    @Test
    public void testManyTransactionsAccounts() throws InterruptedException, SQLException {
        User user = new User();
        Account acc0 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc0);
        Account acc1 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc1);
        Account acc2 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc2);
        Account acc3 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc3);
        Account acc4 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc4);
        Account acc5 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc5);
        Account acc6 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc6);
        Account acc7 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc7);
        Account acc8 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc8);
        Account acc9 = new Account(user, BigDecimal.valueOf(1000));
        accountDao.create(acc9);

        List<Runnable> transactions = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < 1_000; i++) {
            transactions.add(() -> transactionService.transact(
                    random.nextInt(10) + 1,
                    random.nextInt(10) + 1,
                    BigDecimal.valueOf(random.nextInt(10) + 1)));
        }

        assertConcurrent(transactions, 10);
    }

    public static void assertConcurrent(final List<? extends Runnable> runnables, final int maxTimeoutSeconds) throws InterruptedException {
        final int numThreads = runnables.size();
        final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());
        final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
        try {
            final CountDownLatch allExecutorThreadsReady = new CountDownLatch(numThreads);
            final CountDownLatch afterInitBlocker = new CountDownLatch(1);
            final CountDownLatch allDone = new CountDownLatch(numThreads);
            for (final Runnable submittedTestRunnable : runnables) {
                threadPool.submit(() -> {
                    allExecutorThreadsReady.countDown();
                    try {
                        afterInitBlocker.await();
                        submittedTestRunnable.run();
                    } catch (final Throwable e) {
                        exceptions.add(e);
                    } finally {
                        allDone.countDown();
                    }
                });
            }
            // wait until all threads are ready
            assertTrue("Timeout initializing threads! Perform long lasting initializations before passing runnables to assertConcurrent", allExecutorThreadsReady.await(runnables.size() * 10, TimeUnit.MILLISECONDS));
            // start all test runners
            afterInitBlocker.countDown();
            assertTrue("Timeout! More than" + maxTimeoutSeconds + "seconds", allDone.await(maxTimeoutSeconds, TimeUnit.SECONDS));
        } finally {
            threadPool.shutdownNow();
        }
        assertTrue("Failed with exception(s)" + exceptions, exceptions.isEmpty());
    }
}
