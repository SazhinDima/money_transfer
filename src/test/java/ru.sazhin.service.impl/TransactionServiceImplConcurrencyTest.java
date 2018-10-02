package ru.sazhin.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.sazhin.model.Account;
import ru.sazhin.model.User;
import ru.sazhin.service.TransactionService;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class TransactionServiceImplConcurrencyTest {

    private TransactionService transactionService;

    @Before
    public void prepare() {
        transactionService = new TransactionServiceImpl();
    }

    @Test
    public void test10Accounts() throws InterruptedException {
        User user = new User();
        Account acc0 = new Account(user, BigDecimal.valueOf(1000));
        Account acc1 = new Account(user, BigDecimal.valueOf(1000));
        Account acc2 = new Account(user, BigDecimal.valueOf(1000));
        Account acc3 = new Account(user, BigDecimal.valueOf(1000));
        Account acc4 = new Account(user, BigDecimal.valueOf(1000));
        Account acc5 = new Account(user, BigDecimal.valueOf(1000));
        Account acc6 = new Account(user, BigDecimal.valueOf(1000));
        Account acc7 = new Account(user, BigDecimal.valueOf(1000));
        Account acc8 = new Account(user, BigDecimal.valueOf(1000));
        Account acc9 = new Account(user, BigDecimal.valueOf(1000));

        List<? extends Runnable> transactions = Arrays.asList(
//                () -> transactionService.transact(acc0, acc1, BigDecimal.valueOf(10)),
//                () -> transactionService.transact(acc2, acc3, BigDecimal.valueOf(15)),
//                () -> transactionService.transact(acc2, acc0, BigDecimal.valueOf(10)),
//                () -> transactionService.transact(acc0, acc1, BigDecimal.valueOf(20)),
//                () -> transactionService.transact(acc7, acc9, BigDecimal.valueOf(10)),
//                () -> transactionService.transact(acc3, acc7, BigDecimal.valueOf(10)),
//                () -> transactionService.transact(acc0, acc1, BigDecimal.valueOf(25)),
//                () -> transactionService.transact(acc4, acc1, BigDecimal.valueOf(10)),
//                () -> transactionService.transact(acc0, acc1, BigDecimal.valueOf(15)),
//                () -> transactionService.transact(acc8, acc3, BigDecimal.valueOf(30)),
//                () -> transactionService.transact(acc5, acc2, BigDecimal.valueOf(35)),
//                () -> transactionService.transact(acc6, acc9, BigDecimal.valueOf(25)),
//                () -> transactionService.transact(acc3, acc2, BigDecimal.valueOf(10)),
//                () -> transactionService.transact(acc5, acc8, BigDecimal.valueOf(15)),
//                () -> transactionService.transact(acc0, acc6, BigDecimal.valueOf(10)),
//                () -> transactionService.transact(acc9, acc1, BigDecimal.valueOf(15))

        );

        assertConcurrent(transactions, 10);

        Assert.assertEquals(BigDecimal.valueOf(930), acc0.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(1095), acc1.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(1020), acc2.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(1025), acc3.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(990), acc4.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(950), acc5.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(985), acc6.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(1000), acc7.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(985), acc8.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(1020), acc9.getAmount());
    }

    @Test
    public void testManyTransactionsAccounts() throws InterruptedException {
        User user = new User();
        List<Account> accounts = Arrays.asList(
            new Account(user, BigDecimal.valueOf(1000)),
            new Account(user, BigDecimal.valueOf(1000)),
            new Account(user, BigDecimal.valueOf(1000)),
            new Account(user, BigDecimal.valueOf(1000)),
            new Account(user, BigDecimal.valueOf(1000)),
            new Account(user, BigDecimal.valueOf(1000)),
            new Account(user, BigDecimal.valueOf(1000)),
            new Account(user, BigDecimal.valueOf(1000)),
            new Account(user, BigDecimal.valueOf(1000)),
            new Account(user, BigDecimal.valueOf(1000))
        );

        List<Runnable> transactions = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < 10_000; i++) {
            Account accountFrom = accounts.get(random.nextInt(10));
            Account accountTo = accounts.get(random.nextInt(10));
            BigDecimal amount = BigDecimal.valueOf(random.nextInt(10) + 1);
            //transactions.add(() -> transactionService.transact(accountFrom, accountTo, amount));
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
